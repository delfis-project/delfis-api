/*
 * Classe PowerupController
 * Controller da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.Powerup;
import com.delfis.apiuserpostgres.service.PowerupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/powerup")
public class PowerupController {
    private final PowerupService powerupService;

    public PowerupController(PowerupService powerupService) {
        this.powerupService = powerupService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getPowerups() {
        List<Powerup> powerups = powerupService.getPowerups();
        if (!powerups.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(powerups);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    public ResponseEntity<?> getPowerupByName(@PathVariable String name) {
        Powerup powerup = powerupService.getPowerupByName(name.strip());
        if (powerup != null) return ResponseEntity.status(HttpStatus.OK).body(powerup);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPowerup(@Valid @RequestBody Powerup powerup) {
        try {
            Powerup savedPowerup = powerupService.savePowerup(powerup);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPowerup);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Powerup com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePowerup(@PathVariable Long id) {
        try {
            if (powerupService.deletePowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Powerup deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse powerup. Mude-os para excluir esse powerup.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePowerup(@PathVariable Long id, @Valid @RequestBody Powerup powerup) {
        if (powerupService.getPowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");

        return insertPowerup(new Powerup(id, powerup.getName(), powerup.getPrice(), powerup.getStorePictureUrl()));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updatePowerupPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Powerup existingPowerup = powerupService.getPowerupById(id);  // validando se existe
        if (existingPowerup == null) throw new EntityNotFoundException("Powerup não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name":
                        existingPowerup.setName((String) value);
                        break;
                    case "price":
                        existingPowerup.setPrice((Integer) value);
                        break;
                    case "storePictureUrl":
                        existingPowerup.setStorePictureUrl((String) value);
                        break;
                    default:
                        throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingPowerup, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        powerupService.savePowerup(existingPowerup);
        return ResponseEntity.status(HttpStatus.OK).body("Powerup atualizado com sucesso.");
    }
}
