/*
 * Classe AppUserPowerupController
 * Controller da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.AppUserPowerup;
import com.delfis.apiuserpostgres.service.AppUserPowerupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/app-user-powerup")
public class AppUserPowerupController {
    private final AppUserPowerupService appUserPowerupService;

    public AppUserPowerupController(AppUserPowerupService appUserPowerupService) {
        this.appUserPowerupService = appUserPowerupService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAppUserPowerups() {
        List<AppUserPowerup> appUserPowerup = appUserPowerupService.getAppUserPowerups();
        if (!appUserPowerup.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserPowerup);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @GetMapping("/get-by-app-user")
    public ResponseEntity<?> getAppUserPowerupsByAppUser(@RequestBody AppUser appUser) {
        List<AppUserPowerup> appUserPowerup = appUserPowerupService.getAppUserPowerupsByAppUser(appUser);
        if (!appUserPowerup.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserPowerup);

        throw new EntityNotFoundException("Nenhum tema encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertAppUserPowerup(@Valid @RequestBody AppUserPowerup appUserPowerup) {
        AppUserPowerup savedAppUserPowerup = appUserPowerupService.saveAppUserPowerup(appUserPowerup);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUserPowerup);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppUserPowerup(@PathVariable Long id) {
        try {
            if (appUserPowerupService.deleteAppUserPowerupById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Tema deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse tema. Mude-os para excluir esse tema.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppUserPowerup(@PathVariable Long id, @Valid @RequestBody AppUserPowerup appUserPowerup) {
        if (appUserPowerupService.getAppUserPowerupById(id) == null) throw new EntityNotFoundException("Tema não encontrado.");

        return insertAppUserPowerup(new AppUserPowerup(
                id,
                appUserPowerup.getTransactionPrice(),
                appUserPowerup.getTransactionDate(),
                appUserPowerup.getAppUser(),
                appUserPowerup.getPowerup()
        ));
    }
}
