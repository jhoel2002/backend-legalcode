package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.CaseStatus;

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
@Table(name = "case_control")
public class CaseControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CaseStatus status_case;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "creation")
    @CreationTimestamp
    private Timestamp creation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lawyer", nullable = false)
    private Lawyer lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_buffet", nullable = false)
    private Buffet buffet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_request", nullable = false)
    private CaseRequest caseRequest;
}
