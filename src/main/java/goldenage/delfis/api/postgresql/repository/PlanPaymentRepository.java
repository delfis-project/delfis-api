/*
 * Classe PlanPaymentRepository
 * Repository da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.PlanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPaymentRepository extends JpaRepository<PlanPayment, Long> {
    List<PlanPayment> findPlanPaymentsByFkAppUserIdOrderByExpirationTimestamp(Long id);
    List<PlanPayment> findPlanPaymentsByFkPlanId(Long id);
}
