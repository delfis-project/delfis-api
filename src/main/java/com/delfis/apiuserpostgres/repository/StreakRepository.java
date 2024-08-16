/*
 * Classe StreakRepository
 * Repository da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    List<Streak> findStreaksByInitalDateBefore(LocalDate initalDate);
    List<Streak> findStreaksByAppUserEquals(AppUser appUser);
}
