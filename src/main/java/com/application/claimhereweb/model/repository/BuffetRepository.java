package com.application.claimhereweb.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.Buffet;

public interface BuffetRepository extends JpaRepository<Buffet, Long> {

    Optional<Buffet> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String code);

    @Query("SELECT b FROM Buffet b JOIN FETCH b.typeCase WHERE b.code = :code")
    Optional<Buffet> findByCodeWithTypeCases(@Param("code") String code);
}
