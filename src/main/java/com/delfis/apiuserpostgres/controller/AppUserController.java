/*
 * Classe AppUserController
 * Controller da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.service.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAppUsers() {
        List<AppUser> users = appUserService.getAppUsers();
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity<?> getAppUserByUsername(@PathVariable String username) {
        AppUser user = appUserService.getAppUserByUsername(username);
        if (user == null) throw new EntityNotFoundException("Nenhum user encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity<?> getAppUserByEmail(@PathVariable String email) {
        AppUser user = appUserService.getAppUserByEmail(email);
        if (user == null) throw new EntityNotFoundException("Nenhum user encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/get-by-plan")
    public ResponseEntity<?> getAppUsersByPlan(@RequestBody Plan plan) {
        List<AppUser> users = appUserService.getAppUsersByPlan(plan);
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/get-by-plan")
    public ResponseEntity<?> getAppUsersByUserRole(@RequestBody UserRole userRole) {
        List<AppUser> users = appUserService.getAppUsersByUserRole(userRole);
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        List<AppUser> users = appUserService.getLeaderboard();
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertAppUser(@Valid @RequestBody AppUser appUser) {
        try {
            AppUser savedAppUser = appUserService.saveAppUser(appUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUser);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("User com esse nome já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppUser(@PathVariable Long id) {
        try {
            if (appUserService.deleteAppUserById(id) == null) throw new EntityNotFoundException("User não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("User deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem tabelas dependentes desse usuário. Mude-as para excluir esse user.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppUser(@PathVariable Long id, @Valid @RequestBody AppUser appUser) {
        if (appUserService.getAppUserById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User não encontrado.");
        }

        return insertAppUser(new AppUser(
                id,
                appUser.getName(),
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.getEmail(),
                appUser.getLevel(),
                appUser.getPoints(),
                appUser.getCoins(),
                appUser.getBirthDate(),
                appUser.getPictureUrl(),
                appUser.getPlan(),
                appUser.getUserRole(),
                appUser.getCreatedAt(),
                appUser.getUpdatedAt()
        ));
    }
}
