/*
 * Classe AppUserPowerupService
 * Service da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.api.postgresql.service;

import goldenage.delfis.api.postgresql.model.AppUserPowerup;
import goldenage.delfis.api.postgresql.repository.AppUserPowerupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserPowerupService {
    private final AppUserPowerupRepository appUserPowerupRepository;

    public AppUserPowerupService(AppUserPowerupRepository appUserPowerupRepository) {
        this.appUserPowerupRepository = appUserPowerupRepository;
    }

    /**
     * @return todos os appUserPowerups do banco.
     */
    public List<AppUserPowerup> getAppUserPowerups() {
        List<AppUserPowerup> appUserPowerups = appUserPowerupRepository.findAll();
        return appUserPowerups.isEmpty() ? null : appUserPowerups;
    }

    /**
     * @return appUserPowerup pelo id.
     */
    public AppUserPowerup getAppUserPowerupById(Long id) {
        Optional<AppUserPowerup> appUserPowerup = appUserPowerupRepository.findById(id);
        return appUserPowerup.orElse(null);  // vai retornar appUserPowerup se ele existir, se não retorna null
    }

    /**
     * @return appUserPowerup pelo appUser.
     */
    public List<AppUserPowerup> getAppUserPowerupsByAppUserId(Long id) {
        List<AppUserPowerup> appUserPowerups = appUserPowerupRepository.findAppUserPowerupsByFkAppUserId(id);
        return !appUserPowerups.isEmpty() ? appUserPowerups : null;
    }


    /**
     * @return appUserPowerup deletado.
     */
    public AppUserPowerup deleteAppUserPowerupById(Long id) {
        AppUserPowerup appUserPowerup = getAppUserPowerupById(id);
        if (appUserPowerup != null) appUserPowerupRepository.deleteById(id);
        return appUserPowerup;
    }

    /**
     * @return appUserPowerup inserido.
     */
    public AppUserPowerup saveAppUserPowerup(AppUserPowerup appUserPowerup) {
        return appUserPowerupRepository.save(appUserPowerup);
    }
}
