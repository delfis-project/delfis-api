/*
 * Classe PlanPaymentController
 * Controller da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.model.PlanPayment;
import com.delfis.apiuserpostgres.service.PlanPaymentService;
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

import java.util.*;

@RestController
@RequestMapping("/api/plan-payment")
@Tag(name = "PlanPayment", description = "Endpoints para gerenciamento de pagamentos de planos")
public class PlanPaymentController {
    private final PlanPaymentService planPaymentService;

    public PlanPaymentController(PlanPaymentService planPaymentService) {
        this.planPaymentService = planPaymentService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os pagamentos de planos", description = "Retorna uma lista de todos os pagamentos de planos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos de planos encontrada", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlanPayments() {
        List<PlanPayment> planPayments = planPaymentService.getPlanPayments();
        if (!planPayments.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @GetMapping("/get-by-app-user")
    @Operation(summary = "Obter pagamentos por usuário", description = "Retorna uma lista de pagamentos de planos associados ao usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos encontrada", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlanPaymentsByAppUser(@RequestBody AppUser appUser) {
        List<PlanPayment> planPayments = planPaymentService.getPlanPaymentsByAppUser(appUser);
        if (planPayments != null) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @GetMapping("/get-by-plan")
    @Operation(summary = "Obter pagamentos por plano", description = "Retorna uma lista de pagamentos de planos associados ao plano fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos encontrada", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlanPaymentsByPlan(@RequestBody Plan plan) {
        List<PlanPayment> planPayments = planPaymentService.getPlanPaymentByPlan(plan);
        if (planPayments != null) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo pagamento de plano", description = "Cria um novo pagamento de plano no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento de plano criado com sucesso", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Pagamento já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertPlanPayment(@Valid @RequestBody PlanPayment planPayment) {
        try {
            PlanPayment savedPlanPayment = planPaymentService.savePlanPayment(planPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlanPayment);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pagamento já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um pagamento de plano", description = "Remove um pagamento de plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem planos cadastrados com esse pagamento", content = @Content)
    })
    public ResponseEntity<String> deletePlanPayment(@PathVariable Long id) {
        try {
            if (planPaymentService.deletePlanPaymentById(id) == null) throw new EntityNotFoundException("Pagamento não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Pagamento deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem planos cadastrados com esse pagamento. Mude-os para excluir esse pagamento.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um pagamento de plano", description = "Atualiza todos os dados de um pagamento de plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento de plano atualizado com sucesso", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePlanPayment(@PathVariable Long id, @Valid @RequestBody PlanPayment planPayment) {
        if (planPaymentService.getPlanPaymentById(id) == null) throw new EntityNotFoundException("Pagamento não encontrado.");

        return insertPlanPayment(new PlanPayment(
                id,
                planPayment.getPrice(),
                planPayment.getPaymentTimestamp(),
                planPayment.getExpirationTimestamp(),
                planPayment.getPlan(),
                planPayment.getAppUser()
        ));
    }
}
