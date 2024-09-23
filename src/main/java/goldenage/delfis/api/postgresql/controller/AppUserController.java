/*
 * Classe AppUserController
 * Controller da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 */

package goldenage.delfis.api.postgresql.controller;

import goldenage.delfis.api.postgresql.model.Address;
import goldenage.delfis.api.postgresql.model.Plan;
import goldenage.delfis.api.postgresql.model.UserRole;
import goldenage.delfis.api.postgresql.util.ControllerUtils;
import goldenage.delfis.api.postgresql.model.AppUser;
import goldenage.delfis.api.postgresql.service.AddressService;
import goldenage.delfis.api.postgresql.service.AppUserService;
import goldenage.delfis.api.postgresql.service.PlanService;
import goldenage.delfis.api.postgresql.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class AppUserController {
    private final AppUserService appUserService;
    private final PlanService planService;
    private final UserRoleService userRoleService;
    private final AddressService addressService;

    public AppUserController(AppUserService appUserService, PlanService planService, UserRoleService userRoleService, AddressService addressService) {
        this.appUserService = appUserService;
        this.planService = planService;
        this.userRoleService = userRoleService;
        this.addressService = addressService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os usuários", description = "Retorna uma lista de todos os usuários registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content)
    })
    public ResponseEntity<?> getAppUsers() {
        List<AppUser> appUsers = appUserService.getAppUsers();
        if (appUsers != null) return ResponseEntity.status(HttpStatus.OK).body(appUsers);

        throw new EntityNotFoundException("Nenhum usuário encontrado.");
    }

    @GetMapping("/get-by-username/{username}")
    @Operation(summary = "Obter usuário por nome de usuário", description = "Retorna um usuário baseado no seu nome de usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUserByUsername(
            @Parameter(description = "Nome de usuário do usuário", required = true)
            @PathVariable String username) {
        AppUser appUser = appUserService.getAppUserByUsername(username);
        if (appUser == null) throw new EntityNotFoundException("Nenhum usuário encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(appUser);
    }

    @GetMapping("/get-by-email/{email}")
    @Operation(summary = "Obter usuário por email", description = "Retorna um usuário baseado no seu email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Argumento inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUserByEmail(
            @Parameter(description = "Email do usuário", required = true)
            @PathVariable String email) {
        AppUser appUser = appUserService.getAppUserByEmail(email);
        if (appUser == null) throw new EntityNotFoundException("Nenhum usuário encontrado.");

        return ResponseEntity.status(HttpStatus.OK).body(appUser);
    }

    @GetMapping("/get-by-plan/{id}")
    @Operation(summary = "Obter usuários por plano", description = "Retorna uma lista de usuários associados a um plano específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID do plano inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUsersByPlanId(
            @Parameter(description = "ID do plano", required = true)
            @PathVariable Long id) {
        List<AppUser> appUsers = appUserService.getAppUsersByPlanId(id);
        if (appUsers != null) return ResponseEntity.status(HttpStatus.OK).body(appUsers);

        throw new EntityNotFoundException("Nenhum usuário encontrado.");
    }

    @GetMapping("/get-by-user-role/{id}")
    @Operation(summary = "Obter usuários por função", description = "Retorna uma lista de usuários associados a uma função específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID da função inválido", content = @Content)
    })
    public ResponseEntity<?> getAppUsersByUserRoleId(
            @Parameter(description = "ID da função do usuário", required = true)
            @PathVariable Long id) {
        List<AppUser> appUsers = appUserService.getAppUsersByUserRoleId(id);
        if (appUsers != null) return ResponseEntity.status(HttpStatus.OK).body(appUsers);

        throw new EntityNotFoundException("Nenhum usuário encontrado.");
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Obter leaderboard", description = "Retorna uma lista de usuários ordenados por pontuação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AppUser.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content)
    })
    public ResponseEntity<?> getLeaderboard() {
        List<AppUser> appUsers = appUserService.getLeaderboard();
        if (appUsers != null) return ResponseEntity.status(HttpStatus.OK).body(appUsers);

        throw new EntityNotFoundException("Nenhum usuário encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo usuário", description = "Cria um novo usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Usuário com nome já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<AppUser> insertAppUser(
            @Parameter(description = "Dados do novo usuário", required = true)
            @Valid @RequestBody AppUser appUser) {
        try {
            appUser.setCreatedAt(LocalDateTime.now());
            appUser.setLevel(1);

            verifyFks(appUser);

            AppUser savedAppUser = appUserService.saveAppUser(appUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUser);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException(dive.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um usuário", description = "Remove um usuário do sistema baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem dependências para esse usuário", content = @Content),
            @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content)
    })
    public ResponseEntity<String> deleteAppUser(
            @Parameter(description = "ID do usuário a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (appUserService.deleteAppUserById(id) == null)
                throw new EntityNotFoundException("Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Usuário deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem dependências para esse usuário. Mude-as para excluir esse usuário.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um usuário", description = "Atualiza todos os dados de um usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<AppUser> updateAppUser(
            @Parameter(description = "ID do usuário a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do usuário", required = true)
            @Valid @RequestBody AppUser appUser) {
        if (appUserService.getAppUserById(id) == null) throw new EntityNotFoundException("Usuário não encontrado.");

        verifyFks(appUser);

        appUser.setId(id);
        appUser.setName(appUser.getName().strip().toUpperCase());
        appUser.setPassword(new BCryptPasswordEncoder().encode(appUser.getPassword()));
        appUser.setUpdatedAt(LocalDateTime.now());

        AppUser updatedAppUser = appUserService.saveAppUser(appUser);
        if (updatedAppUser == null) throw new RuntimeException("Erro durante atualização");
        return ResponseEntity.status(HttpStatus.OK).body(updatedAppUser);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um usuário", description = "Atualiza parcialmente os dados de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUser.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePartialAppUser(
            @Parameter(description = "ID do usuário a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Dados parciais para atualização", required = true)
            @RequestBody Map<String, Object> updates) {
        AppUser existingAppUser = appUserService.getAppUserById(id);  // validando se existe
        if (existingAppUser == null) throw new EntityNotFoundException("Usuário não encontrado.");

        existingAppUser.setId(id);
        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name" -> existingAppUser.setName(((String) value).strip().toUpperCase());
                    case "username" -> existingAppUser.setUsername((String) value);
                    case "email" -> existingAppUser.setEmail((String) value);
                    case "password" -> existingAppUser.setPassword((String) value);
                    case "level" -> existingAppUser.setLevel((Integer) value);
                    case "points" -> existingAppUser.setPoints((Integer) value);
                    case "coins" -> existingAppUser.setCoins((Integer) value);
                    case "birthDate" -> existingAppUser.setBirthDate(LocalDate.parse((String) value));
                    case "pictureUrl" -> existingAppUser.setPictureUrl((String) value);
                    case "fkPlanId" -> {
                        Plan plan = planService.getPlanById(((Integer) value).longValue());
                        if (plan == null) throw new ClassCastException("Plano não encontrado.");
                        existingAppUser.setFkPlanId(plan.getId());
                    }
                    case "fkUserRoleId" -> {
                        UserRole userRole = userRoleService.getUserRoleById(((Integer) value).longValue());
                        if (userRole == null) throw new ClassCastException("Role não encontrado.");
                        existingAppUser.setFkUserRoleId(userRole.getId());
                    }
                    case "fkAddressId" -> {
                        Address address = addressService.getAddressById(((Integer) value).longValue());
                        if (address == null) throw new ClassCastException("Endereço não encontrado.");
                        existingAppUser.setFkAddressId(address.getId());
                    }
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se algum campo está incorreto
        Map<String, String> errors = ControllerUtils.verifyObject(existingAppUser, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        existingAppUser.setUpdatedAt(LocalDateTime.now());
        AppUser appUserInserted = appUserService.saveAppUser(existingAppUser);
        if (appUserInserted == null) throw new RuntimeException("Erro durante inserção");
        return ResponseEntity.status(HttpStatus.OK).body(appUserInserted);
    }

    private void verifyFks(AppUser appUser) {
        Plan plan = planService.getPlanById((appUser.getFkPlanId()));
        if (plan == null) throw new EntityNotFoundException("Plano não encontrado.");
        appUser.setFkPlanId(plan.getId());

        UserRole userRole = userRoleService.getUserRoleById(appUser.getFkUserRoleId());
        if (userRole == null) throw new EntityNotFoundException("Role não encontrado.");
        appUser.setFkUserRoleId(userRole.getId());

        Address address = addressService.getAddressById(appUser.getFkAddressId());
        if (address == null) throw new EntityNotFoundException("Endereço não encontrado.");
        appUser.setFkAddressId(address.getId());
    }
}
