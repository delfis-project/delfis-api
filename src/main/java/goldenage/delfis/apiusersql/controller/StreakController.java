/*
 * Classe StreakController
 * Controller da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Streak;
import goldenage.delfis.apiusersql.service.StreakService;
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

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/streak")
@Tag(name = "Streak", description = "Endpoints para gerenciamento de streaks")
public class StreakController {
    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os streaks", description = "Retorna uma lista de todos os streaks registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<?> getStreaks() {
        List<Streak> streaks = streakService.getStreaks();
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @GetMapping("/get-by-initial-date-before")
    @Operation(summary = "Obter streaks com data inicial anterior", description = "Retorna uma lista de streaks cuja data inicial é anterior à fornecida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<?> getStreaksByInitialDateBefore(@RequestBody LocalDate initialDate) {
        List<Streak> streaks = streakService.getStreaksByInitalDateBefore(initialDate);
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @GetMapping("/get-by-app-user")
    @Operation(summary = "Obter streaks por usuário", description = "Retorna uma lista de streaks associados ao usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de streaks encontrada", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum streak encontrado", content = @Content)
    })
    public ResponseEntity<?> getStreaksByAppUser(@RequestBody AppUser appUser) {
        List<Streak> streaks = streakService.getStreaksByAppUser(appUser);
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo streak", description = "Cria um novo streak no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Streak criado com sucesso", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Streak já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertStreak(@Valid @RequestBody Streak streak) {
        try {
            Streak savedStreak = streakService.saveStreak(streak);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStreak);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Streak com esse já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um streak", description = "Remove um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse streak", content = @Content)
    })
    public ResponseEntity<String> deleteStreak(@PathVariable Long id) {
        try {
            if (streakService.deleteStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Streak deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse streak. Mude-os para excluir esse streak.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um streak", description = "Atualiza todos os dados de um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak atualizado com sucesso", content = @Content(schema = @Schema(implementation = Streak.class))),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateStreak(@PathVariable Long id, @Valid @RequestBody Streak streak) {
        if (streakService.getStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");

        streakService.saveStreak(streak);
        return ResponseEntity.status(HttpStatus.OK).body(streak);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um streak", description = "Atualiza parcialmente os dados de um streak baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Streak atualizado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Streak não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateStreakPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Streak existingStreak = streakService.getStreakById(id);  // validando se existe
        if (existingStreak == null) throw new EntityNotFoundException("Streak não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "initialDate":
                        existingStreak.setInitialDate((LocalDate) value);
                        break;
                    case "finalDate":
                        existingStreak.setFinalDate((LocalDate) value);
                        break;
                    default:
                        throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // Validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingStreak, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        streakService.saveStreak(existingStreak);
        return ResponseEntity.status(HttpStatus.OK).body("Streak atualizado com sucesso.");
    }
}
