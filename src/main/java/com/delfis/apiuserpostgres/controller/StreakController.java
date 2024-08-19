/*
 * Classe StreakController
 * Controller da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Streak;
import com.delfis.apiuserpostgres.service.StreakService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/streak")
public class StreakController {
    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getStreaks() {
        List<Streak> streaks = streakService.getStreaks();
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @GetMapping("/get-by-inital-date-before")
    public ResponseEntity<?> getStreaksByInitialDateBefore(@RequestBody LocalDate initalDate) {
        List<Streak> streaks = streakService.getStreaksByInitalDateBefore(initalDate);
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @GetMapping("/get-by-app-user")
    public ResponseEntity<?> getStreaksByAppUser(@RequestBody AppUser appUser) {
        List<Streak> streaks = streakService.getStreaksByAppUser(appUser);
        if (!streaks.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(streaks);

        throw new EntityNotFoundException("Nenhum streak encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertStreak(@Valid @RequestBody Streak streak) {
        try {
            Streak savedStreak = streakService.saveStreak(streak);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStreak);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Streak com esse já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStreak(@PathVariable Long id) {
        try {
            if (streakService.deleteStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Streak deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse streak. Mude-os para excluir esse streak.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStreak(@PathVariable Long id, @Valid @RequestBody Streak streak) {
        if (streakService.getStreakById(id) == null) throw new EntityNotFoundException("Streak não encontrado.");

        return insertStreak(new Streak(
                id,
                streak.getInitalDate(),
                streak.getFinalDate(),
                streak.getAppUser()
        ));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateStreakPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Streak existingStreak = streakService.getStreakById(id);  // validando se existe
        if (existingStreak == null) throw new EntityNotFoundException("Streak não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "initialDate":
                        existingStreak.setInitalDate((LocalDate) value);
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

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingStreak, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        streakService.saveStreak(existingStreak);
        return ResponseEntity.status(HttpStatus.OK).body("Streak atualizado com sucesso.");
    }
}
