package com.application.claimhereweb.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.TypeCase;

public interface TypeCaseRepository extends JpaRepository<TypeCase, Long> {
    Optional<TypeCase> findByName(String name);
}