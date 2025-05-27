package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.Role;

import jakarta.transaction.Transactional;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users_roles (id_users, id_role) VALUES (:userId, :roleId)", nativeQuery = true)
    void assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
