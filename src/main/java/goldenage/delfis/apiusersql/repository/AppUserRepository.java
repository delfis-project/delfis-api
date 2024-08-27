/*
 * Classe AppUserRepository
 * Repository da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByEmailEqualsIgnoreCase(String email);
    Optional<AppUser> findAppUserByUsernameEqualsIgnoreCase(String username);
    List<AppUser> findAppUsersByPlan_IdEquals(Long id);
    List<AppUser> findAppUsersByUserRole_IdEquals(Long id);
    List<AppUser> findAllByOrderByPointsDesc();
}
