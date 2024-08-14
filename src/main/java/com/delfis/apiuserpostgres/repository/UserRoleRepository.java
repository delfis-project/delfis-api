/*
 * Classe UserRoleRepository
 * Repository da entidade UserRole
 * Autor: João Diniz Araujo
 * Data: 13/08/2024
 * */

package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.UserRole;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
