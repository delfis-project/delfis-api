/*
 * Classe AppUserRepository
 * Repository da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByEmailEqualsIgnoreCase(String email);
    Optional<AppUser> findAppUserByUsernameEqualsIgnoreCase(String username);
    List<AppUser> findAppUsersByFkPlanIdEquals(Long id);
    List<AppUser> findAppUsersByFkUserRoleIdEquals(Long id);
    List<AppUser> findTop50ByOrderByPointsDesc();
}
