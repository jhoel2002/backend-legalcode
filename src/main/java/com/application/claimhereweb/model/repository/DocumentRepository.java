package com.application.claimhereweb.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.claimhereweb.model.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    boolean existsByCode(String code);

    Optional<Document> findByCode(String code);

    @Query("""
                SELECT d FROM Document d
                JOIN d.case_request cr
                WHERE cr.code = :code AND d.type_document = 'QUOTATION'
            """)
    Optional<Document> findQuotationByCaseRequestCode(@Param("code") String code);

    @Query("""
                SELECT d FROM Document d
                JOIN d.case_request cr
                WHERE cr.code = :code AND d.type_document = 'EVIDENCE'
            """)
    List<Document> findEvidenceByCaseRequestCode(@Param("code") String code);
}
