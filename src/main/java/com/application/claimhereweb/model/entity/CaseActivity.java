package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.ActivityType;

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
@Table(name = "case_activity")
public class CaseActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "registration_date", nullable = false)
    @CreationTimestamp
    private Timestamp registration_date;

    @Column(name = "type_activity", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType type_activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_legal_case", referencedColumnName = "id", nullable = false)
    private LegalCase legal_case;

}
