/*
 * Classe UserRoleController
 * Controller da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.service.UserRoleService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getUserRoles() {
        List<UserRole> users = userRoleService.getUserRoles();
        return users.size() > 0
                ? ResponseEntity.status(HttpStatus.OK).body(users)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhuma role encontrada.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertUserRole(@Valid @RequestBody UserRole userRole) {
        try {
            UserRole savedUserRole = userRoleService.saveUserRole(userRole);
            return ResponseEntity.status(HttpStatus.OK).body(savedUserRole);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Role com esse nome já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUserRole(@PathVariable Long id) {
        try {
            if (userRoleService.deleteUserRoleById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role não encontrado.");
            }
            return ResponseEntity.status(HttpStatus.OK).body("Role deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Existem usuários cadastrados com essa role. Mude-os para excluir essa role.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRole userRole) {
        if (userRoleService.getUserRoleById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role não encontrado.");
        }

        return insertUserRole(new UserRole(id, userRole.getName()));
    }
}
