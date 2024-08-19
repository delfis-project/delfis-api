/*
 * Classe UserRoleRepository
 * Repository da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
