/*
 * Classe AppUserThemeController
 * Controller da entidade AppUserTheme
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.AppUserTheme;
import goldenage.delfis.apiusersql.service.AppUserThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app-user-theme")
@Tag(name = "AppUserTheme", description = "Endpoints para gerenciamento de temas de usuários")
public class AppUserThemeController {
    private final AppUserThemeService appUserThemeService;

    public AppUserThemeController(AppUserThemeService appUserThemeService) {
        this.appUserThemeService = appUserThemeService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os temas de usuários", description = "Retorna uma lista de todos os temas de usuários registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas encontrada", content = @Content(schema = @Schema(implementation = AppUserTheme.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum tema encontrado", content = @Content)
    })
    public ResponseEntity<?> getAppUserThemes() {
        List<AppUserTheme> appUserThemes = appUserThemeService.getAppUserThemes();
        if (!appUserThemes.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserThemes);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-app-user")
    @Operation(summary = "Obter temas por usuário", description = "Retorna uma lista de temas baseados no usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas encontrada", content = @Content(schema = @Schema(implementation = AppUserTheme.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum tema encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> getAppUserThemesByAppUser(@RequestBody AppUser appUser) {
        List<AppUserTheme> appUserThemes = appUserThemeService.getAppUserThemesByAppUser(appUser);
        if (!appUserThemes.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserThemes);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo tema de usuário", description = "Cria um novo tema de usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema criado com sucesso", content = @Content(schema = @Schema(implementation = AppUserTheme.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertAppUserTheme(@Valid @RequestBody AppUserTheme appUserTheme) {
        AppUserTheme savedAppUserTheme = appUserThemeService.saveAppUserTheme(appUserTheme);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUserTheme);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um tema de usuário", description = "Remove um tema de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse tema", content = @Content)
    })
    public ResponseEntity<String> deleteAppUserTheme(@PathVariable Long id) {
        try {
            if (appUserThemeService.deleteAppUserThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Tema deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse tema. Mude-os para excluir esse tema.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um tema de usuário", description = "Atualiza todos os dados de um tema de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUserTheme.class))),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateAppUserTheme(@PathVariable Long id, @Valid @RequestBody AppUserTheme appUserTheme) {
        if (appUserThemeService.getAppUserThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");

        appUserTheme.setId(id);
        appUserThemeService.saveAppUserTheme(appUserTheme);
        return ResponseEntity.status(HttpStatus.OK).body(appUserTheme);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um tema de usuário", description = "Atualiza parcialmente um tema de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema atualizado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tema não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateAppUserThemePartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        AppUserTheme existingAppUserTheme = appUserThemeService.getAppUserThemeById(id);  // validando se existe
        if (existingAppUserTheme == null) throw new EntityNotFoundException("Tema não encontrado.");

        existingAppUserTheme.setId(id);
        updates.forEach((key, value) -> {
            try {
                if (key.equals("isInUse")) {
                    existingAppUserTheme.setInUse((boolean) value);
                } else {
                    throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingAppUserTheme, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        appUserThemeService.saveAppUserTheme(existingAppUserTheme);
        return ResponseEntity.status(HttpStatus.OK).body("Tema atualizado com sucesso.");
    }
}