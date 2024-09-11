package goldenage.delfis.auth.service;

import goldenage.delfis.apiusersql.model.AppUser;
import goldenage.delfis.apiusersql.model.UserRole;
import goldenage.delfis.apiusersql.service.AppUserService;
import goldenage.delfis.apiusersql.service.UserRoleService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserService appUserService;
    private final UserRoleService userRoleService;

    public CustomUserDetailsService(AppUserService appUserService, UserRoleService userRoleService) {
        this.appUserService = appUserService;
        this.userRoleService = userRoleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserService.getAppUserByUsername(username);
        UserRole userRole = userRoleService.getUserRoleById(appUser.getFkUserRoleId());

        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                appUser.getPassword(),
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.getName()))
        );
    }
}
