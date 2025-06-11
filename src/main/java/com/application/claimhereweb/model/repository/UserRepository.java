package com.application.claimhereweb.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByCode(String code);
}