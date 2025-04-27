package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.DocumentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_document", nullable = false)
    private String name_document;

    @Column(name = "url_document", nullable = false)
    private String url_document;

    @Column(name = "type_document", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType type_document;

    @Column(name = "upload_date", nullable = false)
    @CreationTimestamp
    private Timestamp upload_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_legal_case", referencedColumnName = "id", nullable = false)
    private LegalCase legal_case;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", referencedColumnName = "id", nullable = true)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_activity", referencedColumnName = "id", nullable = true)
    private CaseActivity case_activity;

}
