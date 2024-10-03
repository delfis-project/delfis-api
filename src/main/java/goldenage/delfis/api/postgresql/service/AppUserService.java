/*
 * Classe AppUserService
 * Service da entidade AppUser
 * Autor: João Diniz Araujo
 * Data: 16/08/2024
 * */

package goldenage.delfis.api.postgresql.service;

import goldenage.delfis.api.postgresql.model.AppUser;
import goldenage.delfis.api.postgresql.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * @return todos os appUsers do banco.
     */
    public List<AppUser> getAppUsers() {
        List<AppUser> appUsers = appUserRepository.findAll();
        return appUsers.isEmpty() ? null : appUsers;
    }

    /**
     * @return appUser pelo nome de usuário.
     */
    public AppUser getAppUserByUsername(String username) {
        Optional<AppUser> appUser = appUserRepository.findAppUserByUsernameEqualsIgnoreCase(username);
        return appUser.orElse(null);
    }

    /**
     * @return appUser pelo nome de usuário.
     */
    public AppUser getAppUserByEmail(String email) {
        Optional<AppUser> appUser = appUserRepository.findAppUserByEmailEqualsIgnoreCase(email);
        return appUser.orElse(null);
    }

    /**
     * @return appUsers pelo plano.
     */
    public List<AppUser> getAppUsersByPlanId(Long id) {
        List<AppUser> appUsers = appUserRepository.findAppUsersByFkPlanIdEquals(id);
        return appUsers.isEmpty() ? null : appUsers;
    }

    /**
     * @return appUsers pelo role.
     */
    public List<AppUser> getAppUsersByUserRoleId(Long id) {
        List<AppUser> appUsers = appUserRepository.findAppUsersByFkUserRoleIdEquals(id);
        return appUsers.isEmpty() ? null : appUsers;
    }

    /**
     * @return appUsers ordenado pelos pontos decrescente.
     */
    public List<AppUser> getLeaderboard() {
        List<AppUser> appUsers = appUserRepository.findAllByOrderByPointsDesc();
        return appUsers.isEmpty() ? null : appUsers;
    }

    /**
     * @return appUser pelo id.
     */
    public AppUser getAppUserById(Long id) {
        Optional<AppUser> appUser = appUserRepository.findById(id);
        return appUser.orElse(null);  // vai retornar appUser se ele existir, se não retorna null
    }

    /**
     * @return appUser deletado.
     */
    public AppUser deleteAppUserById(Long id) {
        AppUser appUser = getAppUserById(id);
        if (appUser != null) appUserRepository.deleteById(id);
        return appUser;
    }

    /**
     * @return appUser inserido.
     */
    public AppUser saveAppUser(AppUser appUser) {
        appUser.setName(appUser.getName().strip().toUpperCase());
        return appUserRepository.save(appUser);
    }
}
