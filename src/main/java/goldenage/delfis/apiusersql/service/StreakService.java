/*
 * Classe StreakService
 * Service da entidade Streak
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.apiusersql.service;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.Streak;
import goldenage.delfis.apiusersql.repository.StreakRepository;
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
        return streakRepository.findAll();
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
    public List<Streak> getStreaksByInitalDateBefore(LocalDate initialDate) {
        List<Streak> streaks = streakRepository.findStreaksByInitalDateBefore(initialDate);
        return !streaks.isEmpty() ? streaks : null;
    }

    /**
     * @return streak pelo usuário.
     */
    public List<Streak> getStreaksByAppUser(AppUser appUser) {
        List<Streak> streaks = streakRepository.findStreaksByAppUserEquals(appUser);
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
