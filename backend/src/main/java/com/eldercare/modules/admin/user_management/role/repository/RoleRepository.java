package com.eldercare.modules.admin.user_management.role.repository;

import com.eldercare.modules.admin.user_management.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findByIsDeletedFalse();

    Optional<RoleEntity> findByRoleName(String roleName);
}
