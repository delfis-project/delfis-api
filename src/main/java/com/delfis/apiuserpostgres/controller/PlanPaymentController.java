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
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/planPayment")
public class PlanPaymentController {
    private final PlanPaymentService planPaymentService;

    public PlanPaymentController(PlanPaymentService planPaymentService) {
        this.planPaymentService = planPaymentService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getPlanPayments() {
        List<PlanPayment> planPayments = planPaymentService.getPlanPayments();
        if (!planPayments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(planPayments);
        }

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @GetMapping("/get-by-app-user")
    public ResponseEntity<?> getPlanPaymentsByAppUser(@RequestBody AppUser appUser) {
        List<PlanPayment> planPayment = planPaymentService.getPlanPaymentsByAppUser(appUser);
        if (planPayment != null) {
            return ResponseEntity.status(HttpStatus.OK).body(planPayment);
        }

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @GetMapping("/get-by-plan")
    public ResponseEntity<?> getPlanPaymentsByAppUser(@RequestBody Plan plan) {
        List<PlanPayment> planPayment = planPaymentService.getPlanPaymentByPlan(plan);
        if (planPayment != null) {
            return ResponseEntity.status(HttpStatus.OK).body(planPayment);
        }

        throw new EntityNotFoundException("Nenhum pagamento encontrado.");
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertPlanPayment(@Valid @RequestBody PlanPayment planPayment) {
        try {
            PlanPayment savedPlanPayment = planPaymentService.savePlanPayment(planPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlanPayment);
        } catch (DataIntegrityViolationException dive) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pagamento existente.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePlanPayment(@PathVariable Long id) {
        try {
            if (planPaymentService.deletePlanPaymentById(id) == null) {
                throw new EntityNotFoundException("Pagamento não encontrado.");
            }
            return ResponseEntity.status(HttpStatus.OK).body("Pagamento deletado com sucesso.");
        } catch (DataIntegrityViolationException dive) {
            throw new DataIntegrityViolationException("Existem planos cadastrados com esse pagamento. Mude-os para excluir esse pagamento.");
        }
    }

    @PutMapping("/update/{id}")
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
