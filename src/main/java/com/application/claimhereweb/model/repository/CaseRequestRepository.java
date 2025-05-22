package com.application.claimhereweb.model.repository;

//import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseRequestRepository extends JpaRepository<CaseRequest, Long> {

    @Query("SELECT cr FROM CaseRequest cr WHERE cr.id = :id")
    Optional<CaseRequest> findCaseRequestById(@Param("id") Long id);

    @Query("SELECT COUNT(cr) > 0 FROM CaseRequest cr WHERE cr.id = :id")
    boolean existsByIdCustom(@Param("id") Long id);

    @Query("SELECT cr FROM CaseRequest cr WHERE cr.status_request = :status")
    Page<CaseRequest> findAllByStatusRequest(@Param("status") CaseStatusRequest statusRequest, Pageable pageable);
}
