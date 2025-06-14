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

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type_document", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentType type_document;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "creation", nullable = false)
    @CreationTimestamp
    private Timestamp creation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_request", referencedColumnName = "id", nullable = true)
    private CaseRequest case_request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_buffet", nullable = false)
    private Buffet buffet;
}