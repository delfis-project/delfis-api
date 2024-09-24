/*
 * Classe ThemeRepository
 * Repository da entidade Theme
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */


package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findThemeByNameEqualsIgnoreCase(String name);
}
