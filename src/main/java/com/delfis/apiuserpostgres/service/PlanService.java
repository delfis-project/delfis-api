/*
 * Classe PlanService
 * Service da entidade Plan
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.service;

import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /**
     * @return planos pelo preço menor ou igual.
     */
    public List<Plan> findPlansByPriceIsGreaterThanEqual(BigDecimal price) {
        List<Plan> plans = planRepository.findPlansByPriceIsGreaterThanEqual(price);
        return !plans.isEmpty() ? plans : null;
    }

    /**
     * @return planos pelo preço menor ou igual.
     */
    public List<Plan> findPlansByPriceIsLessThanEqual(BigDecimal price) {
        List<Plan> plans = planRepository.findPlansByPriceIsLessThanEqual(price);
        return !plans.isEmpty() ? plans : null;
    }

    /**
     * @return plano pelo nome.
     */
    public Plan findPlanByName(String name) {
        Optional<Plan> plan = planRepository.findPlanByNameEqualsIgnoreCase(name);
        return plan.orElse(null);
    }

    /**
     * @return todos os planos do banco.
     */
    public List<Plan> getPlans() {
        return planRepository.findAll();
    }

    /**
     * @return plan pelo id.
     */
    public Plan getPlanById(Long id) {
        Optional<Plan> plan = planRepository.findById(id);
        return plan.orElse(null);  // vai retornar plan se ele existir, se não retorna null
    }

    /**
     * @return plan deletado.
     */
    public Plan deletePlanById(Long id) {
        Plan plan = getPlanById(id);
        if (plan != null) planRepository.deleteById(id);
        return plan;
    }

    /**
     * @return plan inserido.
     */
    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }
}
