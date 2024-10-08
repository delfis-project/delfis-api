/*
 * Classe PlanPaymentService
 * Service da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.service;

import goldenage.delfis.api.postgresql.model.PlanPayment;
import goldenage.delfis.api.postgresql.repository.PlanPaymentRepository;
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
        List<PlanPayment> planPayments = planPaymentRepository.findAll();
        return planPayments.isEmpty() ? null : planPayments;
    }

    /**
     * @return planPayment pelo usuário.
     */
    public List<PlanPayment> getPlanPaymentsByAppUserId(Long id) {
        List<PlanPayment> planPayment = planPaymentRepository
                .findPlanPaymentsByFkAppUserIdOrderByExpirationTimestamp(id);
        return planPayment.isEmpty() ? null : planPayment;
    }

    /**
     * @return planPayment pelo plano.
     */
    public List<PlanPayment> getPlanPaymentByPlanId(Long id) {
        List<PlanPayment> planPayment = planPaymentRepository.findPlanPaymentsByFkPlanId(id);
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
