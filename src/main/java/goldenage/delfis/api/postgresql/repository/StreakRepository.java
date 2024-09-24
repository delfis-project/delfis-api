/*
 * Classe StreakRepository
 * Repository da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.repository;

import goldenage.delfis.api.postgresql.model.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StreakRepository extends JpaRepository<Streak, Long> {
    List<Streak> findStreaksByInitialDateBefore(LocalDate initialDate);
    List<Streak> findStreaksByFkAppUserId(Long id);
}