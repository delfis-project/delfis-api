/*
 * Classe StreakController
 * Controller da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.controller;

import goldenage.delfis.api.postgresql.model.Streak;
import goldenage.delfis.api.postgresql.util.ControllerUtils;
import goldenage.delfis.api.postgresql.model.AppUser;
import goldenage.delfis.api.postgresql.service.AppUserService;
import goldenage.delfis.api.postgresql.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/streak")
public class StreakController {
    private final StreakService streakService;
    private final AppUserService appUserService;

    public StreakController(StreakService streakService, AppUserService appUserService) {
        this.streakService = streakService;
        this.appUserService = appUserService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os streaks", description = "Retorna uma lista de todos os streaks registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Streak.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<List<Streak>> getStreaks() {
        List<Streak> streaks = streakService.getStreaks();
        if (streaks != null) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @GetMapping("/get-by-initial-date-before/{initialDate}")
    @Operation(summary = "Obter streaks com data inicial anterior", description = "Retorna uma lista de streaks cuja data inicial é anterior à fornecida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Streak.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<List<Streak>> getStreaksByInitialDateBefore(
            @Parameter(description = "Data inicial para filtragem dos streaks", required = true)
            @PathVariable LocalDate initialDate) {
        List<Streak> streaks = streakService.getStreaksByInitialDateBefore(initialDate);
        if (streaks != null) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado com a data inicial fornecida.");
    }

    @GetMapping("/get-by-app-user/{id}")
    @Operation(summary = "Obter streaks por usuário", description = "Retorna uma lista de streaks associados ao usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Streak.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<List<Streak>> getStreaksByAppUser(
            @Parameter(description = "ID do usuário para filtragem dos streaks", required = true)
            @PathVariable Long id) {
        List<Streak> streaks = streakService.getStreaksByAppUserId(id);
        if (streaks != null) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado para o usuário fornecido.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo streak", description = "Cria um novo streak no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Streak criado com sucesso", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Streak já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Streak> insertStreak(
            @Parameter(description = "Dados do novo streak", required = true)
            @Valid @RequestBody Streak streak) {
        try {
            verifyFk(streak);
            Streak savedStreak = streakService.saveStreak(streak);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStreak);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um streak", description = "Remove um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse streak", content = @Content)
    })
    public ResponseEntity<String> deleteStreak(
            @Parameter(description = "ID do streak a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (streakService.deleteStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Streak deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Existem usuários cadastrados com esse streak. Mude-os para excluir esse streak.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um streak", description = "Atualiza todos os dados de um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak atualizado com sucesso", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Streak> updateStreak(
            @Parameter(description = "ID do streak a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do streak", required = true)
            @Valid @RequestBody Streak streak) {
        if (streakService.getStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");

        verifyFk(streak);
        streak.setId(id);
        Streak updatedStreak = streakService.saveStreak(streak);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStreak);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um streak", description = "Atualiza parcialmente os dados de um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak atualizado com sucesso", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateStreakPartially(
            @Parameter(description = "ID do streak a ser atualizado parcialmente", required = true)
            @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados", required = true)
            @RequestBody Map<String, Object> updates) {
        Streak existingStreak = streakService.getStreakById(id);
        if (existingStreak == null) throw new EntityNotFoundException("Streak não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "initialDate" -> existingStreak.setInitialDate(LocalDate.parse((String) value));
                    case "finalDate" -> existingStreak.setFinalDate(LocalDate.parse((String) value));
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // Validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingStreak, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        Streak updatedStreak = streakService.saveStreak(existingStreak);
        return ResponseEntity.status(HttpStatus.OK).body(updatedStreak);
    }

    private void verifyFk(Streak streak) {
        AppUser appUser = appUserService.getAppUserById((streak.getFkAppUserId()));
        if (appUser == null) throw new EntityNotFoundException("Usuário não encontrado.");
        streak.setFkAppUserId(appUser.getId());
    }
}
