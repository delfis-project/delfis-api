/*
 * Classe AppUserThemeRepository
 * Repository da entidade AppUserTheme
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.AppUserTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserThemeRepository extends JpaRepository<AppUserTheme, Long> {
    List<AppUserTheme> findAppUserThemesByAppUser(AppUser appUser);
    Optional<AppUserTheme> findAppUserThemeByAppUserAndInUse(AppUser appUser, boolean inUse);
}
