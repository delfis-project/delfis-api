/*
 * Classe PlanPaymentRepository
 * Repository da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.model.PlanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPaymentRepository extends JpaRepository<PlanPayment, Long> {
    List<PlanPayment> findPlanPaymentsByAppUserOrderByExpirationTimestamp(AppUser appUser);
    List<PlanPayment> findPlanPaymentsByPlan(Plan plan);
}
