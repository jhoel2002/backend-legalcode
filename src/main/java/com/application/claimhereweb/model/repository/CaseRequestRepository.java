package com.application.claimhereweb.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.CaseRequest;

public interface CaseRequestRepository extends JpaRepository<CaseRequest, Long> {

    @Query("SELECT cr FROM CaseRequest cr WHERE cr.id = :id")
    Optional<CaseRequest> findCaseRequestById(@Param("id") Long id);

    @Query("SELECT COUNT(cr) > 0 FROM CaseRequest cr WHERE cr.id = :id")
    boolean existsByIdCustom(@Param("id") Long id);
}
