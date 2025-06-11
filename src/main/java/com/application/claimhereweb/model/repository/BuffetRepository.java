package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.Buffet;

public interface BuffetRepository extends JpaRepository<Buffet, Long> {

    boolean existsByCode(String code);

    boolean existsByName(String code);
}
