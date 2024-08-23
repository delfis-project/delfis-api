/*
 * Classe PowerupController
 * Controller da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.Powerup;
import goldenage.delfis.apiusersql.service.PowerupService;
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
@RequestMapping("/api/powerup")
@Tag(name = "Powerup", description = "Endpoints para gerenciamento de powerups")
public class PowerupController {
    private final PowerupService powerupService;

    public PowerupController(PowerupService powerupService) {
        this.powerupService = powerupService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os powerups", description = "Retorna uma lista de todos os powerups registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de powerups encontrada", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<?> getPowerups() {
        List<Powerup> powerups = powerupService.getPowerups();
        if (!powerups.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(powerups);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    @Operation(summary = "Obter powerup por nome", description = "Retorna o powerup com o nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup encontrado", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<?> getPowerupByName(@PathVariable String name) {
        Powerup powerup = powerupService.getPowerupByName(name.strip());
        if (powerup != null) return ResponseEntity.status(HttpStatus.OK).body(powerup);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo powerup", description = "Cria um novo powerup no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Powerup criado com sucesso", content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Powerup já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertPowerup(@Valid @RequestBody Powerup powerup) {
        try {
            powerup.setName(powerup.getName().strip());
            Powerup savedPowerup = powerupService.savePowerup(powerup);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPowerup);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Powerup com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um powerup", description = "Remove um powerup baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse powerup", content = @Content)
    })
    public ResponseEntity<String> deletePowerup(@PathVariable Long id) {
        try {
            if (powerupService.deletePowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");
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
    public ResponseEntity<?> updatePowerup(@PathVariable Long id, @Valid @RequestBody Powerup powerup) {
        if (powerupService.getPowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");

        powerup.setId(id);
        powerup.setName(powerup.getName().strip());
        powerupService.savePowerup(powerup);
        return ResponseEntity.status(HttpStatus.OK).body(powerup);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um powerup", description = "Atualiza parcialmente os dados de um powerup baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup atualizado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePowerupPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Powerup existingPowerup = powerupService.getPowerupById(id);  // validando se existe
        if (existingPowerup == null) throw new EntityNotFoundException("Powerup não encontrado.");

        existingPowerup.setId(id);
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

        powerupService.savePowerup(existingPowerup);
        return ResponseEntity.status(HttpStatus.OK).body("Powerup atualizado com sucesso.");
    }
}
