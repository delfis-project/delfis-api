/*
 * Classe AppUserThemeController
 * Controller da entidade AppUserTheme
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.AppUserTheme;
import com.delfis.apiuserpostgres.service.AppUserThemeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/app-user-theme")
public class AppUserThemeController {
    private final AppUserThemeService appUserThemeService;

    public AppUserThemeController(AppUserThemeService appUserThemeService) {
        this.appUserThemeService = appUserThemeService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAppUserThemes() {
        List<AppUserTheme> appUserTheme = appUserThemeService.getAppUserThemes();
        if (!appUserTheme.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserTheme);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-app-user")
    public ResponseEntity<?> getAppUserThemesByAppUser(@RequestBody AppUser appUser) {
        List<AppUserTheme> appUserTheme = appUserThemeService.getAppUserThemesByAppUser(appUser);
        if (!appUserTheme.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserTheme);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-app-user-and-is-in-use/{isInUse}")
    public ResponseEntity<?> getAppUserThemeByAppUserAndIsInUse(@RequestBody AppUser appUser, @PathVariable boolean isInUse) {
        AppUserTheme appUserTheme = appUserThemeService.getAppUserThemeByAppUserAndIsInUse(appUser, isInUse);
        if (appUserTheme != null) return ResponseEntity.status(HttpStatus.OK).body(appUserTheme);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertAppUserTheme(@Valid @RequestBody AppUserTheme appUserTheme) {
        AppUserTheme savedAppUserTheme = appUserThemeService.saveAppUserTheme(appUserTheme);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUserTheme);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppUserTheme(@PathVariable Long id) {
        try {
            if (appUserThemeService.deleteAppUserThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Tema deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse tema. Mude-os para excluir esse tema.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppUserTheme(@PathVariable Long id, @Valid @RequestBody AppUserTheme appUserTheme) {
        if (appUserThemeService.getAppUserThemeById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");

        return insertAppUserTheme(new AppUserTheme(
                id, 
                appUserTheme.isInUse(),
                appUserTheme.getTransactionPrice(),
                appUserTheme.getTransactionDate(),
                appUserTheme.getAppUser(),
                appUserTheme.getTheme()
        ));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateAppUserThemePartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        AppUserTheme existingAppUserTheme = appUserThemeService.getAppUserThemeById(id);  // validando se existe
        if (existingAppUserTheme == null) throw new EntityNotFoundException("Tema não encontrado.");

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
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        appUserThemeService.saveAppUserTheme(existingAppUserTheme);
        return ResponseEntity.status(HttpStatus.OK).body("Tema atualizado com sucesso.");
    }
}
