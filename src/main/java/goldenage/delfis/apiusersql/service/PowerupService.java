/*
 * Classe PowerupService
 * Service da entidade Powerup
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package goldenage.delfis.apiusersql.service;

import goldenage.delfis.apiusersql.model.Plan;
import goldenage.delfis.apiusersql.model.Powerup;
import goldenage.delfis.apiusersql.repository.PowerupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PowerupService {
    private final PowerupRepository powerupRepository;

    public PowerupService(PowerupRepository powerupRepository) {
        this.powerupRepository = powerupRepository;
    }

    /**
     * @return todos os powerups do banco.
     */
    public List<Powerup> getPowerups() {
        List<Powerup> powerups = powerupRepository.findAll();
        return powerups.isEmpty() ? null : powerups;
    }

    /**
     * @return powerup pelo nome.
     */
    public Powerup getPowerupByName(String name) {
        Optional<Powerup> powerup = powerupRepository.findPowerupByNameEqualsIgnoreCase(name);
        return powerup.orElse(null);
    }

    /**
     * @return powerup pelo id.
     */
    public Powerup getPowerupById(Long id) {
        Optional<Powerup> powerup = powerupRepository.findById(id);
        return powerup.orElse(null);  // vai retornar powerup se ele existir, se não retorna null
    }

    /**
     * @return powerup deletado.
     */
    public Powerup deletePowerupById(Long id) {
        Powerup powerup = getPowerupById(id);
        if (powerup != null) powerupRepository.deleteById(id);
        return powerup;
    }

    /**
     * @return powerup inserido.
     */
    public Powerup savePowerup(Powerup powerup) {
        return powerupRepository.save(powerup);
    }
}
