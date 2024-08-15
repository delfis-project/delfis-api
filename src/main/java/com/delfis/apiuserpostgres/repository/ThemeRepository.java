/*
 * Classe ThemeRepository
 * Repository da entidade Theme
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */


package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findThemeByNameEqualsIgnoreCase(String name);
}
