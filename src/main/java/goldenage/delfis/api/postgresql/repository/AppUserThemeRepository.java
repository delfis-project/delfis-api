/*
 * Classe AppUserThemeRepository
 * Repository da entidade AppUserTheme
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.AppUserTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserThemeRepository extends JpaRepository<AppUserTheme, Long> {
    List<AppUserTheme> findAppUserThemesByFkAppUserId(Long id);
}
