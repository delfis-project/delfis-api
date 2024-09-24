/*
 * Classe AppUserPowerupRepository
 * Repository da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.AppUserPowerup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserPowerupRepository extends JpaRepository<AppUserPowerup, Long> {
    List<AppUserPowerup> findAppUserPowerupsByFkAppUserId(Long id);
}
