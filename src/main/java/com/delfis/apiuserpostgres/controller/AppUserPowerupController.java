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
@RequestMapping("/api/app-user-powerup")
@Tag(name = "AppUserPowerup", description = "Endpoints para gerenciamento de powerups de usuários")
public class AppUserPowerupController {
    private final AppUserPowerupService appUserPowerupService;

    public AppUserPowerupController(AppUserPowerupService appUserPowerupService) {
        this.appUserPowerupService = appUserPowerupService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os powerups de usuários", description = "Retorna uma lista de todos os powerups de usuários registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de powerups encontrada", content = @Content(schema = @Schema(implementation = AppUserPowerup.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content)
    })
    public ResponseEntity<?> getAppUserPowerups() {
        List<AppUserPowerup> appUserPowerups = appUserPowerupService.getAppUserPowerups();
        if (!appUserPowerups.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserPowerups);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @GetMapping("/get-by-app-user")
    @Operation(summary = "Obter powerups por usuário", description = "Retorna uma lista de powerups baseados no usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de powerups encontrada", content = @Content(schema = @Schema(implementation = AppUserPowerup.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum powerup encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> getAppUserPowerupsByAppUser(@RequestBody AppUser appUser) {
        List<AppUserPowerup> appUserPowerups = appUserPowerupService.getAppUserPowerupsByAppUser(appUser);
        if (!appUserPowerups.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(appUserPowerups);

        throw new EntityNotFoundException("Nenhum powerup encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo powerup de usuário", description = "Cria um novo powerup de usuário no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Powerup criado com sucesso", content = @Content(schema = @Schema(implementation = AppUserPowerup.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertAppUserPowerup(@Valid @RequestBody AppUserPowerup appUserPowerup) {
        AppUserPowerup savedAppUserPowerup = appUserPowerupService.saveAppUserPowerup(appUserPowerup);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppUserPowerup);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um powerup de usuário", description = "Remove um powerup de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse powerup", content = @Content)
    })
    public ResponseEntity<String> deleteAppUserPowerup(@PathVariable Long id) {
        try {
            if (appUserPowerupService.deleteAppUserPowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Powerup deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse powerup. Mude-os para excluir esse powerup.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um powerup de usuário", description = "Atualiza todos os dados de um powerup de usuário baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Powerup atualizado com sucesso", content = @Content(schema = @Schema(implementation = AppUserPowerup.class))),
            @ApiResponse(responseCode = "404", description = "Powerup não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updateAppUserPowerup(@PathVariable Long id, @Valid @RequestBody AppUserPowerup appUserPowerup) {
        if (appUserPowerupService.getAppUserPowerupById(id) == null) throw new EntityNotFoundException("Powerup não encontrado.");

        return insertAppUserPowerup(new AppUserPowerup(
                id,
                appUserPowerup.getTransactionPrice(),
                appUserPowerup.getTransactionDate(),
                appUserPowerup.getAppUser(),
                appUserPowerup.getPowerup()
        ));
    }
}
