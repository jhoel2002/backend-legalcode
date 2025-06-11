package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;

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
@Table(name = "case_request")
public class CaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private CaseType type_case;

    @Enumerated(EnumType.STRING)
    private CaseStatusRequest status_request = CaseStatusRequest.RECEIVED;

    @Column(name = "creation", nullable = false)
    @CreationTimestamp
    private Timestamp creation;

    @Column(name = "quotation_approved")
    private boolean quotation_approved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_buffet", nullable = false)
    private Buffet buffet;
}
