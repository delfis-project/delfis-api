/*
 * Classe PlanController
 * Controller da entidade Plan
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.service.PlanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getPlans() {
        List<Plan> plans = planService.getPlans();
        if (!plans.isEmpty()) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plan encontrado.");
    }

    @GetMapping("/get-by-name/{name}")
    public ResponseEntity<?> getPlanByName(@PathVariable String name) {
        Plan plan = planService.getPlanByName(name.strip());
        if (plan != null) return ResponseEntity.status(HttpStatus.OK).body(plan);

        throw new EntityNotFoundException("Nenhum plan encontrado.");
    }

    @GetMapping("/get-by-price-less-than-equal/{value}")
    public ResponseEntity<?> getPlansByPriceIsLessThanEqual(@PathVariable BigDecimal value) {
        List<Plan> plans = planService.getPlansByPriceIsLessThanEqual(value);
        if (plans != null) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plan encontrado.");
    }

    @GetMapping("/get-by-price-greater-than-equal/{value}")
    public ResponseEntity<?> getPlansByPriceIsGreaterThanEqual(@PathVariable BigDecimal value) {
        List<Plan> plans = planService.getPlansByPriceIsGreaterThanEqual(value);
        if (plans != null) return ResponseEntity.status(HttpStatus.OK).body(plans);

        throw new EntityNotFoundException("Nenhum plan encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPlan(@Valid @RequestBody Plan plan) {
        try {
            Plan savedPlan = planService.savePlan(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlan);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Plan com esse nome ou foto já existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Long id) {
        try {
            if (planService.deletePlanById(id) == null) throw new EntityNotFoundException("Plan não encontrado.");
            return ResponseEntity.status(HttpStatus.OK).body("Plan deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem usuários cadastrados com esse plan. Mude-os para excluir esse plan.");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePlan(@PathVariable Long id, @Valid @RequestBody Plan plan) {
        if (planService.getPlanById(id) == null) throw new EntityNotFoundException("Plan não encontrado.");

        return insertPlan(new Plan(id, plan.getName(), plan.getPrice(), plan.getDescription()));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updatePlanPartially(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Plan existingPlan = planService.getPlanById(id);  // validando se existe
        if (existingPlan == null) throw new EntityNotFoundException("Plan não encontrado.");

        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "name" -> existingPlan.setName((String) value);
                    case "price" -> existingPlan.setPrice(BigDecimal.valueOf(Double.parseDouble((String) value)));
                    case "description" -> existingPlan.setDescription((String) value);
                    default -> throw new IllegalArgumentException("Campo " + key + " não é atualizável.");
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Valor inválido para o campo " + key + ": " + e.getMessage(), e);
            }
        });

        // validando se ele mandou algum campo errado
        Map<String, String> errors = ControllerUtils.verifyObject(existingPlan, new ArrayList<>(updates.keySet()));
        if (!errors.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        planService.savePlan(existingPlan);
        return ResponseEntity.status(HttpStatus.OK).body("Plan atualizado com sucesso.");
    }
}
