package com.application.claimhereweb.model.repository;

import java.sql.Timestamp;
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

  Optional<CaseRequest> findByCode(String code);

  @Query("SELECT cr FROM CaseRequest cr WHERE cr.id = :id")
  Optional<CaseRequest> findCaseRequestById(@Param("id") Long id);

  @Query("SELECT COUNT(cr) > 0 FROM CaseRequest cr WHERE cr.id = :id")
  boolean existsByIdCustom(@Param("id") Long id);

  @Query("SELECT cr FROM CaseRequest cr WHERE cr.status_request = :status")
  Page<CaseRequest> findAllByStatusRequest(@Param("status") CaseStatusRequest statusRequest, Pageable pageable);

  @Query("""
          SELECT cr FROM CaseRequest cr
          WHERE cr.creation BETWEEN :startDate AND :endDate
      """)
  Page<CaseRequest> findCaseRequestByApplicationDateBetween(
      @Param("startDate") Timestamp startDate,
      @Param("endDate") Timestamp endDate,
      Pageable pageable);

  @Query("""
          SELECT cr FROM CaseRequest cr
          JOIN cr.customer c
          JOIN c.user u
          WHERE LOWER(cr.title) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(cr.description) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(STR(cr.type_case)) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(STR(cr.status_request)) LIKE LOWER(CONCAT('%', :search, '%'))
             OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
      """)
  Page<CaseRequest> searchCaseRequest(@Param("search") String search, Pageable pageable);

  @Query("""
          SELECT cr FROM CaseRequest cr
          JOIN cr.customer c
          JOIN c.user u
          WHERE (LOWER(cr.title) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(cr.description) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(STR(cr.type_case)) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(STR(cr.status_request)) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')))
            AND cr.creation BETWEEN :startDate AND :endDate
            AND cr.status_request = :status
      """)
  Page<CaseRequest> findByFilters(
      @Param("search") String search,
      @Param("startDate") Timestamp startDate,
      @Param("endDate") Timestamp endDate,
      @Param("status") CaseStatusRequest statusRequest,
      Pageable pageable);

}