package com.application.claimhereweb.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.CaseStatus;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;

import java.sql.Timestamp;
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
@Table(name = "legal_case")
public class LegalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status_case", nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseStatus status_case;

    @Column(name = "type_case", nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseType type_case;

    @Column(name = "start_date", nullable = false)
    @CreationTimestamp
    private Timestamp start_date;

    @Column(name = "end_date", nullable = true)
    private Timestamp end_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_customer", referencedColumnName = "id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_case_request", referencedColumnName = "id", nullable = true)
    private CaseRequest case_request;
}
