/*
 * Classe AppUserController
 * Controller da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.model.Streak;
import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.service.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app-user")
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
                appUser.getUpdatedAt(),
                appUser.getStreaks()
        ));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateAppUserPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        AppUser existingAppUser = appUserService.getAppUserById(id);  // validando se existe
        if (existingAppUser == null) throw new EntityNotFoundException("User não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name":
                        existingAppUser.setName((String) value);
                        break;
                    case "username":
                        existingAppUser.setUsername((String) value);
                        break;
                    case "password":
                        existingAppUser.setPassword((String) value);
                        break;
                    case "level":
                        existingAppUser.setLevel((Integer) value);
                        break;
                    case "points":
                        existingAppUser.setPoints((Integer) value);
                        break;
                    case "coins":
                        existingAppUser.setCoins((Integer) value);
                        break;
                    case "birthDate":
                        existingAppUser.setBirthDate((LocalDate) value);
                        break;
                    case "pictureUrl":
                        existingAppUser.setPictureUrl((String) value);
                        break;
                    case "plan":
                        existingAppUser.setPlan((Plan) value);
                        break;
                    case "userRole":
                        existingAppUser.setUserRole((UserRole) value);
                        break;
                    default:
                        throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingAppUser, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        existingAppUser.setUpdatedAt(LocalDateTime.now());
        appUserService.saveAppUser(existingAppUser);
        return ResponseEntity.status(HttpStatus.OK).body("User atualizado com sucesso.");
    }
}
