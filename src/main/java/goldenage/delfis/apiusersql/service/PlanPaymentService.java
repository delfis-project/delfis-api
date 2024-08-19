/*
 * Classe PlanPaymentService
 * Service da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.service;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.model.PlanPayment;
import goldenage.delfis.apiusersql.repository.PlanPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanPaymentService {
    private final PlanPaymentRepository planPaymentRepository;

    public PlanPaymentService(PlanPaymentRepository planPaymentRepository) {
        this.planPaymentRepository = planPaymentRepository;
    }

    /**
     * @return todos os planPayments do banco.
     */
    public List<PlanPayment> getPlanPayments() {
        return planPaymentRepository.findAll();
    }

    /**
     * @return planPayment pelo usuário.
     */
    public List<PlanPayment> getPlanPaymentsByAppUser(AppUser appUser) {
        List<PlanPayment> planPayment = planPaymentRepository
                .findPlanPaymentsByAppUserOrderByExpirationTimestamp(appUser);
        return planPayment.isEmpty() ? null : planPayment;
    }

    /**
     * @return planPayment pelo plano.
     */
    public List<PlanPayment> getPlanPaymentByPlan(Plan plan) {
        List<PlanPayment> planPayment = planPaymentRepository.findPlanPaymentsByPlan(plan);
        return planPayment.isEmpty() ? null : planPayment;
    }

    /**
     * @return planPayment pelo id.
     */
    public PlanPayment getPlanPaymentById(Long id) {
        Optional<PlanPayment> planPayment = planPaymentRepository.findById(id);
        return planPayment.orElse(null);  // vai retornar planPayment se ele existir, se não retorna null
    }

    /**
     * @return planPayment deletado.
     */
    public PlanPayment deletePlanPaymentById(Long id) {
        PlanPayment planPayment = getPlanPaymentById(id);
        if (planPayment != null) planPaymentRepository.deleteById(id);
        return planPayment;
    }

    /**
     * @return planPayment inserido.
     */
    public PlanPayment savePlanPayment(PlanPayment planPayment) {
        return planPaymentRepository.save(planPayment);
    }
}
