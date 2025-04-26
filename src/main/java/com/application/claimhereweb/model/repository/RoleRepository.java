package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query(value = """
                SELECT r.name
                FROM users u
                INNER JOIN users_roles ur ON ur.id_users = u.id
                INNER JOIN roles r ON r.id = ur.id_role
                WHERE u.id = :userId
            """, nativeQuery = true)
    String findRoleNameByUserId(@Param("userId") Long id);

}
