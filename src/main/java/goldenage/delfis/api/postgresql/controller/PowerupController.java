/*
 * Classe PowerupController
 * Controller da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.api.postgresql.controller;

import goldenage.delfis.api.postgresql.model.AppUserPowerup;
import goldenage.delfis.api.postgresql.model.Powerup;
import goldenage.delfis.api.postgresql.service.AppUserPowerupService;
import goldenage.delfis.api.postgresql.util.ControllerUtils;
import goldenage.delfis.api.postgresql.service.PowerupService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/powerup")
public class PowerupController {
    private final PowerupService powerupService;
    private final AppUserPowerupService appUserPowerupService;

    public PowerupController(PowerupService powerupService, AppUserPowerupService appUserPowerupService) {
        this.powerupService = powerupService;
        this.appUserPowerupService = appUserPowerupService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os powerups", description = "Retorna uma lista de todos os powerups registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de powerups encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Powerup.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<List<Powerup>> getPowerups() {
        List<Powerup> powerups = powerupService.getPowerups();
        if (powerups != null) return ResponseEntity.status(HttpStatus.OK).body(powerups);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    @Operation(summary = "Obter powerup por nome", description = "Retorna o powerup com o nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup encontrado", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<Powerup> getPowerupByName(
            @Parameter(description = "Nome do powerup a ser buscado", required = true)
            @PathVariable String name) {
        Powerup powerup = powerupService.getPowerupByName(name.strip());
        if (powerup != null) return ResponseEntity.status(HttpStatus.OK).body(powerup);

        throw new EntityNotFoundException("Nenhum powerup encontrado com o nome fornecido.");
    }

    @GetMapping("/get-by-app-user/{id}")
    @Operation(summary = "Obter powerups por usuário", description = "Retorna a lista de powerups associados a um usuário com o ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerups encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Powerup.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<List<Powerup>> getPowerupsByAppUserId(
            @Parameter(description = "ID do usuário cujos powerups devem ser buscados", required = true)
            @PathVariable Long id) {

        List<AppUserPowerup> appUserPowerups = appUserPowerupService.getAppUserPowerupsByAppUserId(id);
        if (appUserPowerups == null) throw new EntityNotFoundException("Nenhum powerup encontrado.");

        List<Powerup> powerups = new ArrayList<>();
        for (AppUserPowerup appUserPowerup : appUserPowerups) {
            powerups.add(powerupService.getPowerupById(appUserPowerup.getFkPowerupId()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(powerups);
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo powerup", description = "Cria um novo powerup no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Powerup criado com sucesso", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Powerup já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Powerup> insertPowerup(
            @Parameter(description = "Dados do novo powerup", required = true)
            @Valid @RequestBody Powerup powerup) {
        try {
            powerup.setName(powerup.getName().strip());
            Powerup createdPowerup = powerupService.savePowerup(powerup);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPowerup);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Powerup com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um powerup", description = "Remove um powerup baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse powerup", content = @Content)
    })
    public ResponseEntity<String> deletePowerup(
            @Parameter(description = "ID do powerup a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (powerupService.deletePowerupById(id) == null)
                throw new EntityNotFoundException("Powerup não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Powerup deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse powerup. Mude-os para excluir esse powerup.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um powerup", description = "Atualiza todos os dados de um powerup baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup atualizado com sucesso", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<Powerup> updatePowerup(
            @Parameter(description = "ID do powerup a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do powerup", required = true)
            @Valid @RequestBody Powerup powerup) {
        if (powerupService.getPowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");

        powerup.setId(id);
        powerup.setName(powerup.getName().strip());
        Powerup updatedPowerup = powerupService.savePowerup(powerup);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPowerup);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um powerup", description = "Atualiza parcialmente os dados de um powerup baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup atualizado com sucesso", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePowerupPartially(
            @Parameter(description = "ID do powerup a ser atualizado parcialmente", required = true)
            @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados", required = true)
            @RequestBody Map<String, Object> updates) {
        Powerup existingPowerup = powerupService.getPowerupById(id);
        if (existingPowerup == null) throw new EntityNotFoundException("Powerup não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name" -> existingPowerup.setName(((String) value).strip());
                    case "price" -> existingPowerup.setPrice((Integer) value);
                    case "storePictureUrl" -> existingPowerup.setStorePictureUrl((String) value);
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // Validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingPowerup, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        Powerup updatedPowerup = powerupService.savePowerup(existingPowerup);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPowerup);
    }
}