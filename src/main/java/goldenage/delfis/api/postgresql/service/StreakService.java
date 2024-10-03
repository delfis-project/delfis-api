/*
 * Classe StreakService
 * Service da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.service;

import goldenage.delfis.api.postgresql.model.Streak;
import goldenage.delfis.api.postgresql.repository.StreakRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StreakService {
    private final StreakRepository streakRepository;

    public StreakService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    /**
     * @return todos os streaks do banco.
     */
    public List<Streak> getStreaks() {
        List<Streak> streaks = streakRepository.findAll();
        return streaks.isEmpty() ? null : streaks;
    }

    /**
     * @return streak pelo id.
     */
    public Streak getStreakById(Long id) {
        Optional<Streak> streak = streakRepository.findById(id);
        return streak.orElse(null);  // vai retornar streak se ele existir, se não retorna null
    }

    /**
     * @return streak pela data de início.
     */
    public List<Streak> getStreaksByInitialDateBefore(LocalDate initialDate) {
        List<Streak> streaks = streakRepository.findStreaksByInitialDateBefore(initialDate);
        return !streaks.isEmpty() ? streaks : null;
    }

    /**
     * @return streak aberto do usuário.
     */
    public Streak getOpenStreakByAppUserId(Long id) {
        return streakRepository.findStreakByFkAppUserIdAndFinalDateEquals(id, null)
                .orElse(null);
    }

    /**
     * @return streak pelo usuário.
     */
    public List<Streak> getStreaksByAppUserId(Long id) {
        List<Streak> streaks = streakRepository.findStreaksByFkAppUserId(id);
        return !streaks.isEmpty() ? streaks : null;
    }

    /**
     * @return streak deletado.
     */
    public Streak deleteStreakById(Long id) {
        Streak streak = getStreakById(id);
        if (streak != null) streakRepository.deleteById(id);
        return streak;
    }

    /**
     * @return streak inserido.
     */
    public Streak saveStreak(Streak streak) {
        return streakRepository.save(streak);
    }
}
