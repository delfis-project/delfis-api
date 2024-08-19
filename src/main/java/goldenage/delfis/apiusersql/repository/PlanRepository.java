/*
 * Classe PlanRepository
 * Repository da entidade Plan
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findPlansByPriceIsLessThanEqual(BigDecimal price);
    List<Plan> findPlansByPriceIsGreaterThanEqual(BigDecimal price);
    Optional<Plan> findPlanByNameEqualsIgnoreCase(String name);
}
