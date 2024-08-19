/*
 * Classe UserRoleController
 * Controller da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.service.UserRoleService;
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

import java.util.List;

@RestController
@RequestMapping("/api/user-role")
@Tag(name = "UserRole", description = "Endpoints para gerenciamento de roles de usuário")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todas as roles de usuário", description = "Retorna uma lista de todas as roles de usuário registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles encontrada", content = @Content(schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "404", description = "Nenhuma role encontrada", content = @Content)
    })
    public ResponseEntity<?> getUserRoles() {
        List<UserRole> userRoles = userRoleService.getUserRoles();
        if (!userRoles.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(userRoles);

        throw new EntityNotFoundException("Nenhuma role encontrada.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir uma nova role de usuário", description = "Cria uma nova role de usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role criada com sucesso", content = @Content(schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Role já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertUserRole(@Valid @RequestBody UserRole userRole) {
        try {
            UserRole savedUserRole = userRoleService.saveUserRole(userRole);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUserRole);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Role com esse nome já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar uma role de usuário", description = "Remove uma role de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deletada com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com essa role", content = @Content)
    })
    public ResponseEntity<String> deleteUserRole(@PathVariable Long id) {
        try {
            if (userRoleService.deleteUserRoleById(id) == null) throw new EntityNotFoundException("Role não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Role deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com essa role. Mude-os para excluir essa role.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar uma role de usuário", description = "Atualiza todos os dados de uma role de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso", content = @Content(schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "404", description = "Role não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @Valid @RequestBody UserRole userRole) {
        if (userRoleService.getUserRoleById(id) == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role não encontrado.");

        return insertUserRole(new UserRole(id, userRole.getName()));
    }
}
