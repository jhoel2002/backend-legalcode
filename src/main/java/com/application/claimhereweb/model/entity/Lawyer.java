package com.application.claimhereweb.model.entity;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "lawyer")
public class Lawyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "case_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseType case_type;

    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}