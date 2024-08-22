/*
 * Classe AppUserController
 * Controller da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.model.UserRole;
import goldenage.delfis.apiusersql.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app-user")
@Tag(name = "AppUser", description = "Endpoints para gerenciamento de usuários")
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os usuários", description = "Retorna uma lista de todos os usuários registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content)
    })
    public ResponseEntity<?> getAppUsers() {
        List<AppUser> users = appUserService.getAppUsers();
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/get-by-username/{username}")
    @Operation(summary = "Obter usuário por username", description = "Retorna um usuário baseado no seu username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUserByUsername(@PathVariable String username) {
        AppUser user = appUserService.getAppUserByUsername(username);
        if (user == null) throw new EntityNotFoundException("Nenhum user encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/get-by-email/{email}")
    @Operation(summary = "Obter usuário por email", description = "Retorna um usuário baseado no seu email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUserByEmail(@PathVariable String email) {
        AppUser user = appUserService.getAppUserByEmail(email);
        if (user == null) throw new EntityNotFoundException("Nenhum user encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/get-by-plan")
    @Operation(summary = "Obter usuários por plano", description = "Retorna uma lista de usuários baseados em seu plano.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUsersByPlan(@RequestBody Plan plan) {
        List<AppUser> users = appUserService.getAppUsersByPlan(plan);
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/get-by-user-role")
    @Operation(summary = "Obter usuários por função", description = "Retorna uma lista de usuários baseados em sua função.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUsersByUserRole(@RequestBody UserRole userRole) {
        List<AppUser> users = appUserService.getAppUsersByUserRole(userRole);
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Obter leaderboard", description = "Retorna uma lista de usuários ordenados por pontuação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getLeaderboard() {
        List<AppUser> users = appUserService.getLeaderboard();
        if (!users.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(users);

        throw new EntityNotFoundException("Nenhum user encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Usuário com nome já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertAppUser(@Valid @RequestBody AppUser appUser) {
        try {
            appUser.setPassword(new BCryptPasswordEncoder().encode(appUser.getPassword()));
            AppUser savedAppUser = appUserService.saveAppUser(appUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUser);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("User com esse nome já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um usuário", description = "Remove um usuário do sistema baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem dependências para esse usuário", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<String> deleteAppUser(@PathVariable Long id) {
        try {
            if (appUserService.deleteAppUserById(id) == null) throw new EntityNotFoundException("User não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("User deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem tabelas dependentes desse usuário. Mude-as para excluir esse user.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um usuário", description = "Atualiza todos os dados de um usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateAppUser(@PathVariable Long id, @Valid @RequestBody AppUser appUser) {
        if (appUserService.getAppUserById(id) == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User não encontrado.");

        appUser.setPassword(new BCryptPasswordEncoder().encode(appUser.getPassword()));
        appUser.setUpdatedAt(LocalDateTime.now());

        appUserService.saveAppUser(appUser);
        return ResponseEntity.status(HttpStatus.OK).body(appUser);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um usuário", description = "Atualiza alguns dados de um usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content)
    })
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
                        String password = new BCryptPasswordEncoder().encode((String) value);
                        existingAppUser.setPassword(password);
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
