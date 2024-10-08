/*
 * Classe PlanPaymentController
 * Controller da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.controller;

import goldenage.delfis.api.postgresql.model.AppUser;
import goldenage.delfis.api.postgresql.model.Plan;
import goldenage.delfis.api.postgresql.model.PlanPayment;
import goldenage.delfis.api.postgresql.service.AppUserService;
import goldenage.delfis.api.postgresql.service.PlanPaymentService;
import goldenage.delfis.api.postgresql.service.PlanService;
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
@RequestMapping("/api/plan-payment")
public class PlanPaymentController {
    private final PlanPaymentService planPaymentService;
    private final PlanService planService;
    private final AppUserService appUserService;

    public PlanPaymentController(PlanPaymentService planPaymentService, PlanService planService, AppUserService appUserService) {
        this.planPaymentService = planPaymentService;
        this.planService = planService;
        this.appUserService = appUserService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os pagamentos de planos", description = "Retorna uma lista de todos os pagamentos de planos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos de planos encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlanPayment.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<List<PlanPayment>> getPlanPayments() {
        List<PlanPayment> planPayments = planPaymentService.getPlanPayments();
        if (planPayments != null) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @GetMapping("/get-by-app-user/{id}")
    @Operation(summary = "Obter pagamentos por usuário", description = "Retorna uma lista de pagamentos de planos associados ao usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlanPayment.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<List<PlanPayment>> getPlanPaymentsByAppUserId(
            @Parameter(description = "ID do usuário para buscar pagamentos", required = true)
            @PathVariable Long id) {
        List<PlanPayment> planPayments = planPaymentService.getPlanPaymentsByAppUserId(id);
        if (planPayments != null) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado para o usuário.");
    }

    @GetMapping("/get-by-plan/{id}")
    @Operation(summary = "Obter pagamentos por plano", description = "Retorna uma lista de pagamentos de planos associados ao plano fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pagamentos encontrada", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlanPayment.class)))),
            @ApiResponse(responseCode = "404", description = "Nenhum pagamento encontrado", content = @Content)
    })
    public ResponseEntity<List<PlanPayment>> getPlanPaymentsByPlanId(
            @Parameter(description = "ID do plano para buscar pagamentos", required = true)
            @PathVariable Long id) {
        List<PlanPayment> planPayments = planPaymentService.getPlanPaymentByPlanId(id);
        if (planPayments != null) return ResponseEntity.status(HttpStatus.OK).body(planPayments);

        throw new EntityNotFoundException("Nenhum pagamento encontrado para o plano.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo pagamento de plano", description = "Cria um novo pagamento de plano no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento de plano criado com sucesso", content = @Content(schema = @Schema(implementation = PlanPayment.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Pagamento já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<PlanPayment> insertPlanPayment(
            @Parameter(description = "Dados do novo pagamento de plano", required = true)
            @Valid @RequestBody PlanPayment planPayment) {
        try {
            verifyFks(planPayment);
            PlanPayment createdPlanPayment = planPaymentService.savePlanPayment(planPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlanPayment);
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Pagamento já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um pagamento de plano", description = "Remove um pagamento de plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem planos cadastrados com esse pagamento", content = @Content)
    })
    public ResponseEntity<String> deletePlanPayment(
            @Parameter(description = "ID do pagamento a ser deletado", required = true)
            @PathVariable Long id) {
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
    public ResponseEntity<PlanPayment> updatePlanPayment(
            @Parameter(description = "ID do pagamento a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do pagamento de plano", required = true)
            @Valid @RequestBody PlanPayment planPayment) {
        if (planPaymentService.getPlanPaymentById(id) == null) throw new EntityNotFoundException("Pagamento não encontrado.");

        verifyFks(planPayment);
        planPayment.setId(id);
        PlanPayment updatedPlanPayment = planPaymentService.savePlanPayment(planPayment);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPlanPayment);
    }

    private void verifyFks(PlanPayment planPayment) {
        Plan plan = planService.getPlanById((planPayment.getFkPlanId()));
        if (plan == null) throw new EntityNotFoundException("Plano não encontrado.");
        planPayment.setFkPlanId(plan.getId());

        AppUser appUser = appUserService.getAppUserById(planPayment.getFkAppUserId());
        if (appUser == null) throw new EntityNotFoundException("Usuário não encontrado.");
        planPayment.setFkAppUserId(appUser.getId());
    }
}
