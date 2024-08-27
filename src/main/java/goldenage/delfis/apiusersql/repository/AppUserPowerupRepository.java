/*
 * Classe AppUserPowerupRepository
 * Repository da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.AppUserPowerup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppUserPowerupRepository extends JpaRepository<AppUserPowerup, Long> {
    List<AppUserPowerup> findAppUserPowerupsByAppUser_Id(Long id);
}
