/*
 * Classe ThemeController
 * Controller da entidade Theme
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.api.postgresql.controller;

import goldenage.delfis.api.postgresql.model.AppUserPowerup;
import goldenage.delfis.api.postgresql.model.AppUserTheme;
import goldenage.delfis.api.postgresql.model.Powerup;
import goldenage.delfis.api.postgresql.service.AppUserThemeService;
import goldenage.delfis.api.postgresql.service.ThemeService;
import goldenage.delfis.api.postgresql.util.ControllerUtils;
import goldenage.delfis.api.postgresql.model.Theme;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/theme")
public class ThemeController {
    private final ThemeService themeService;
    private final AppUserThemeService appUserThemeService;

    public ThemeController(ThemeService themeService, AppUserThemeService appUserThemeService) {
        this.themeService = themeService;
        this.appUserThemeService = appUserThemeService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os temas", description = "Retorna uma lista de todos os temas registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Theme.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum tema encontrado", content = @Content)
    })
    public ResponseEntity<List<Theme>> getThemes() {
        List<Theme> themes = themeService.getThemes();
        if (themes != null) return ResponseEntity.ok(themes);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    @Operation(summary = "Obter tema por nome", description = "Retorna um tema com base no nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado", content = @Content(schema = @Schema(implementation = Theme.class))),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content)
    })
    public ResponseEntity<Theme> getThemeByName(
            @Parameter(description = "Nome do tema a ser buscado", required = true)
            @PathVariable String name) {
        Theme theme = themeService.getThemeByName(name.strip());
        if (theme != null) return ResponseEntity.ok(theme);

        throw new EntityNotFoundException("Tema com o nome fornecido não encontrado.");
    }

    @GetMapping("/get-themes-by-app-user/{id}")
    @Operation(summary = "Obter temas por usuário", description = "Retorna a lista de temas associados a um usuário com o ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temas encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Theme.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum tema encontrado", content = @Content)
    })
    public ResponseEntity<List<Theme>> getThemesByAppUserId(
            @Parameter(description = "ID do usuário cujos temas devem ser buscados", required = true)
            @PathVariable Long id) {

        List<AppUserTheme> appUserThemes = appUserThemeService.getAppUserThemesByAppUserId(id);
        if (appUserThemes == null) throw new EntityNotFoundException("Nenhum tema encontrado.");

        List<Theme> themes = new ArrayList<>();
        for (AppUserTheme appUserTheme : appUserThemes) {
            themes.add(themeService.getThemeById(appUserTheme.getFkThemeId()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(themes);
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo tema", description = "Cria um novo tema no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema criado com sucesso", content = @Content(schema = @Schema(implementation = Theme.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Tema já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Theme> insertTheme(
            @Parameter(description = "Dados do novo tema", required = true)
            @Valid @RequestBody Theme theme) {
        try {
            theme.setName(theme.getName().strip());
            themeService.saveTheme(theme);
            return ResponseEntity.status(HttpStatus.CREATED).body(theme);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Tema com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um tema", description = "Remove um tema baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse tema", content = @Content)
    })
    public ResponseEntity<String> deleteTheme(
            @Parameter(description = "ID do tema a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (themeService.deleteThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");
            return ResponseEntity.ok("Tema deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Existem usuários cadastrados com esse tema. Mude-os para excluir esse tema.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um tema", description = "Atualiza todos os dados de um tema baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso", content = @Content(schema = @Schema(implementation = Theme.class))),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Theme> updateTheme(
            @Parameter(description = "ID do tema a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do tema", required = true)
            @Valid @RequestBody Theme theme) {
        if (themeService.getThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");

        theme.setId(id);
        theme.setName(theme.getName().strip());
        themeService.saveTheme(theme);
        return ResponseEntity.ok(theme);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um tema", description = "Atualiza parcialmente os dados de um tema baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso", content = @Content(schema = @Schema(implementation = Theme.class))),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateThemePartially(
            @Parameter(description = "ID do tema a ser atualizado parcialmente", required = true)
            @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados", required = true)
            @RequestBody Map<String, Object> updates) {
        Theme existingTheme = themeService.getThemeById(id);
        if (existingTheme == null) throw new EntityNotFoundException("Tema não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name" -> existingTheme.setName(((String) value).strip());
                    case "price" -> existingTheme.setPrice((Integer) value);
                    case "storePictureUrl" -> existingTheme.setStorePictureUrl((String) value);
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // Validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingTheme, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        themeService.saveTheme(existingTheme);
        return ResponseEntity.ok(existingTheme);
    }
}
