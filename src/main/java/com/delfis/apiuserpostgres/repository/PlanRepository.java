package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findPlansByPriceIsLessThanEqual(BigDecimal price);
    List<Plan> findPlansByPriceIsGreaterThanEqual(BigDecimal price);
    Optional<Plan> findPlanByNameEqualsIgnoreCase(String name);
}
