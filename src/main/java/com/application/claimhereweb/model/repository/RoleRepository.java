package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
