/*
 * Classe StreakRepository
 * Repository da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.repository;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    List<Streak> findStreaksByInitalDateBefore(LocalDate initalDate);
    List<Streak> findStreaksByAppUserEquals(AppUser appUser);
}
