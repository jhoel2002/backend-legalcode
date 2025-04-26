package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.LegalCase;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {

    // @Query("SELECT c.user.name FROM Customer c WHERE c.id = :id")
    @Query("SELECT lc.title FROM LegalCase lc WHERE lc.id = :id")
    String findTitleById(@Param("id") Long id);
}
