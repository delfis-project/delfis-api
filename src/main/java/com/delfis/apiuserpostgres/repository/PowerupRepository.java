/*
 * Classe PowerupRepository
 * Repository da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */


package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.Powerup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PowerupRepository extends JpaRepository<Powerup, Long> {
    Optional<Powerup> findPowerupByNameEqualsIgnoreCase(String name);
}
