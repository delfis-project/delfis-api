/*
 * Classe AppUserPowerupRepository
 * Repository da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.AppUserPowerup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserPowerupRepository extends JpaRepository<AppUserPowerup, Long> {
    List<AppUserPowerup> findAppUserPowerupsByAppUser(AppUser appUser);
}
