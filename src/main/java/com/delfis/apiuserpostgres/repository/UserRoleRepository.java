package com.delfis.apiuserpostgres.repository;

import com.delfis.apiuserpostgres.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
