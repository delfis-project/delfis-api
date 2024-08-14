package com.delfis.apiuserpostgres.service;

import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository){
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * @return todos os userRoles do banco.
     */
    public List<UserRole> getUserRoles(){
        return userRoleRepository.findAll();
    }

    /**
     * @return userRole pelo id.
     */
    public UserRole getUserRoleById(long id){
        return userRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("UserRole não encontrado."));
    }

    /**
     * @return userRole deletado.
     */
    public UserRole deleteUserRoleById(long id){
        Optional<UserRole> userRole = userRoleRepository.findById(id);
        if(userRole.isPresent()){
            userRoleRepository.deleteById(id);
            return userRole.get();
        }
        return null;
    }

    /**
     * @return userRole inserido.
     */
    public UserRole saveUserRole(UserRole userRole){
        return userRoleRepository.save(userRole);
    }
}
