/*
 * Classe AppUserThemeService
 * Service da entidade AppUserTheme
 * Autor: João Diniz Araujo
 * Data: 15/08/2024
 * */

package com.delfis.apiuserpostgres.service;

import com.delfis.apiuserpostgres.model.AppUser;
import com.delfis.apiuserpostgres.model.AppUserTheme;
import com.delfis.apiuserpostgres.repository.AppUserThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserThemeService {
    private final AppUserThemeRepository appUserThemeRepository;

    public AppUserThemeService(AppUserThemeRepository appUserThemeRepository) {
        this.appUserThemeRepository = appUserThemeRepository;
    }

    /**
     * @return todos os appUserThemes do banco.
     */
    public List<AppUserTheme> getAppUserThemes() {
        return appUserThemeRepository.findAll();
    }

    /**
     * @return appUserTheme pelo id.
     */
    public AppUserTheme getAppUserThemeById(Long id) {
        Optional<AppUserTheme> appUserTheme = appUserThemeRepository.findById(id);
        return appUserTheme.orElse(null);  // vai retornar appUserTheme se ele existir, se não retorna null
    }

    /**
     * @return appUserTheme pelo appUser.
     */
    public List<AppUserTheme> getAppUserThemeByAppUser(AppUser appUser) {
        List<AppUserTheme> appUserThemes = appUserThemeRepository.findAppUserThemesByAppUser(appUser);
        return !appUserThemes.isEmpty() ? appUserThemes : null;
    }

    /**
     * @return appUserTheme pelo appUser e se ele está em uso.
     */
    public AppUserTheme getAppUserThemeByAppUserAndIsInUse(AppUser appUser, boolean isInUse) {
        Optional<AppUserTheme> appUserTheme = appUserThemeRepository.findAppUserThemeByAppUserAndInUse(appUser, isInUse);
        return appUserTheme.orElse(null);
    }

    /**
     * @return appUserTheme deletado.
     */
    public AppUserTheme deleteAppUserThemeById(Long id) {
        AppUserTheme appUserTheme = getAppUserThemeById(id);
        if (appUserTheme != null) appUserThemeRepository.deleteById(id);
        return appUserTheme;
    }

    /**
     * @return appUserTheme inserido.
     */
    public AppUserTheme saveAppUserTheme(AppUserTheme appUserTheme) {
        return appUserThemeRepository.save(appUserTheme);
    }
}