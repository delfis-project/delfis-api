/*
 * Classe AppUserRepository
 * Repository da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Plan;
import com.delfis.apiuserpostgres.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByEmailEqualsIgnoreCase(String email);
    Optional<AppUser> findAppUserByUsernameEqualsIgnoreCase(String username);
    List<AppUser> findAppUsersByPlanEquals(Plan plan);
    List<AppUser> findAppUsersByUserRoleEquals(UserRole userRole);
    List<AppUser> findAllByOrderByPointsDesc();
}
