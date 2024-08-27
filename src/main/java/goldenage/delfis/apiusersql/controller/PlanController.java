/*
 * Classe PlanController
 * Controller da entidade Plan
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.controller;

import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/get-all")
    @Operation(summary = "Obter todos os planos", description = "Retorna uma lista de todos os planos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planos encontrada", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum plano encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlans() {
        List<Plan> plans = planService.getPlans();
        if (!plans.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plano encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    @Operation(summary = "Obter plano por nome", description = "Retorna um plano baseado no nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano encontrado", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum plano encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlanByName(
            @Parameter(description = "Nome do plano a ser buscado", required = true)
            @PathVariable String name) {
        Plan plan = planService.getPlanByName(name.strip());
        if (plan != null) return ResponseEntity.status(HttpStatus.OK).body(plan);

        throw new EntityNotFoundException("Nenhum plano encontrado.");
    }

    @GetMapping("/get-by-price-less-than-equal/{value}")
    @Operation(summary = "Obter planos por preço menor ou igual", description = "Retorna uma lista de planos com preço menor ou igual ao valor fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planos encontrada", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum plano encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlansByPriceIsLessThanEqual(
            @Parameter(description = "Valor máximo do preço", required = true)
            @PathVariable BigDecimal value) {
        List<Plan> plans = planService.getPlansByPriceIsLessThanEqual(value);
        if (plans != null && !plans.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plano encontrado.");
    }

    @GetMapping("/get-by-price-greater-than-equal/{value}")
    @Operation(summary = "Obter planos por preço maior ou igual", description = "Retorna uma lista de planos com preço maior ou igual ao valor fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planos encontrada", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum plano encontrado", content = @Content)
    })
    public ResponseEntity<?> getPlansByPriceIsGreaterThanEqual(
            @Parameter(description = "Valor mínimo do preço", required = true)
            @PathVariable BigDecimal value) {
        List<Plan> plans = planService.getPlansByPriceIsGreaterThanEqual(value);
        if (plans != null && !plans.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plano encontrado.");
    }

    @PostMapping("/insert")
    @Operation(summary = "Inserir um novo plano", description = "Cria um novo plano no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plano criado com sucesso", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "409", description = "Conflito - Plano com o mesmo nome ou foto já existente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> insertPlan(
            @Parameter(description = "Dados do novo plano", required = true)
            @Valid @RequestBody Plan plan) {
        try {
            plan.setName(plan.getName().strip());
            Plan savedPlan = planService.savePlan(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlan);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Plano com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Deletar um plano", description = "Remove um plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito - Existem usuários cadastrados com esse plano", content = @Content)
    })
    public ResponseEntity<String> deletePlan(
            @Parameter(description = "ID do plano a ser deletado", required = true)
            @PathVariable Long id) {
        try {
            if (planService.deletePlanById(id) == null) throw new EntityNotFoundException("Plano não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Plano deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse plano. Mude-os para excluir esse plano.");
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Atualizar um plano", description = "Atualiza todos os dados de um plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano atualizado com sucesso", content = @Content(schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePlan(
            @Parameter(description = "ID do plano a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do plano", required = true)
            @Valid @RequestBody Plan plan) {
        if (planService.getPlanById(id) == null) throw new EntityNotFoundException("Plano não encontrado.");

        plan.setId(id);
        plan.setName(plan.getName().strip());
        planService.savePlan(plan);
        return ResponseEntity.status(HttpStatus.OK).body(plan);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Atualizar parcialmente um plano", description = "Atualiza parcialmente um plano baseado no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano atualizado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<?> updatePlanPartially(
            @Parameter(description = "ID do plano a ser parcialmente atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Campos a serem atualizados", required = true)
            @RequestBody Map<String, Object> updates) {
        Plan existingPlan = planService.getPlanById(id);
        if (existingPlan == null) throw new EntityNotFoundException("Plano não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name" -> existingPlan.setName(((String) value).strip());
                    case "price" -> {
                        try {
                            existingPlan.setPrice(BigDecimal.valueOf((Double) value));
                        } catch (ClassCastException cce) {
                            existingPlan.setPrice(BigDecimal.valueOf((Integer) value));
                        }
                    }
                    case "description" -> existingPlan.setDescription((String) value);
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        Map<String, String> errors = ControllerUtils.verifyObject(existingPlan, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        planService.savePlan(existingPlan);
        return ResponseEntity.status(HttpStatus.OK).body("Plano atualizado com sucesso.");
    }
}
