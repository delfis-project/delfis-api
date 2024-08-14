package com.delfis.apiuserpostgres.controller;

import com.delfis.apiuserpostgres.model.UserRole;
import com.delfis.apiuserpostgres.service.UserRoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-role")
public class UserRoleController {
    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * @return todos os userRoles do banco.
     */
    @GetMapping("/get-user-roles")
    public List<UserRole> getUserRoles() {
        return userRoleService.getUserRoles();
    }


}
