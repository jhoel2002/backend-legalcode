package com.application.claimhereweb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.claimhereweb.model.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    boolean existsByCode(String code);
}
