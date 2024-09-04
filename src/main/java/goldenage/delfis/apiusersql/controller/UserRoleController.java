/*
 * Classe UserRoleController
 * Controller da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.UserRole;
import goldenage.delfis.apiusersql.service.UserRoleService;
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

import java.util.List;

@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todas as roles de usuário", description = "Retorna uma lista de todas as roles de usuário registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserRole.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhuma role encontrada", content = @Content)
    })
    public ResponseEntity<List<UserRole>> getUserRoles() {
        List<UserRole> userRoles = userRoleService.getUserRoles();
        if (userRoles != null) return ResponseEntity.ok(userRoles);

        throw new EntityNotFoundException("Nenhuma role encontrada.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir uma nova role de usuário", description = "Cria uma nova role de usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role criada com sucesso", content = @Content(schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Role já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<UserRole> insertUserRole(
            @Parameter(description = "Dados da nova role de usuário", required = true)
            @Valid @RequestBody UserRole userRole) {
        try {
            userRole.setName(userRole.getName().strip().toUpperCase());
            UserRole savedUserRole = userRoleService.saveUserRole(userRole);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUserRole);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar uma role de usuário", description = "Remove uma role de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role deletada com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com essa role", content = @Content)
    })
    public ResponseEntity<String> deleteUserRole(
            @Parameter(description = "ID da role de usuário a ser deletada", required = true)
            @PathVariable Long id) {
        try {
            if (userRoleService.deleteUserRoleById(id) == null) throw new EntityNotFoundException("Role não encontrada.");
            return ResponseEntity.status(HttpStatus.OK).body("Role deletada com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Existem usuários cadastrados com essa role. Mude-os para excluir essa role.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar uma role de usuário", description = "Atualiza todos os dados de uma role de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso", content = @Content(schema = @Schema(implementation = UserRole.class))),
            @ApiResponse(responseCode = "404", description = "Role não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<UserRole> updateUserRole(
            @Parameter(description = "ID da role de usuário a ser atualizada", required = true)
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados da role de usuário", required = true)
            @Valid @RequestBody UserRole userRole) {
        if (userRoleService.getUserRoleById(id) == null) throw new EntityNotFoundException("Role não encontrada.");

        userRole.setId(id);
        userRole.setName(userRole.getName().strip().toUpperCase());
        UserRole updatedUserRole = userRoleService.saveUserRole(userRole);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserRole);
    }
}
