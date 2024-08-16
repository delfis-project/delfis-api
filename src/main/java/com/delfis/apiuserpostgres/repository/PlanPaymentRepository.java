/*
 * Classe PlanPaymentRepository
 * Repository da entidade PlanPayment
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.model.PlanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanPaymentRepository extends JpaRepository<PlanPayment, Long> {
    List<PlanPayment> findPlanPaymentsByAppUserOrderByExpirationTimestamp(AppUser appUser);
    List<PlanPayment> findPlanPaymentsByPlan(Plan plan);
}
