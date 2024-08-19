/*
 * Classe AppUserPowerupService
 * Service da entidade AppUserPowerup
 * Autor: João Diniz Araujo
 * Data: 19/08/2024
 * */

package goldenage.delfis.apiusersql.service;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.AppUserPowerup;
import goldenage.delfis.apiusersql.repository.AppUserPowerupRepository;
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
        return appUserPowerupRepository.findAll();
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
    public List<AppUserPowerup> getAppUserPowerupsByAppUser(AppUser appUser) {
        List<AppUserPowerup> appUserPowerups = appUserPowerupRepository.findAppUserPowerupsByAppUser(appUser);
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
