package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status_case", nullable = false)
    private String status_case;

    @Column(name = "creation")
    @CreationTimestamp
    private Timestamp creation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_activity", nullable = false)
    private TypeActivity type_activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lawyer", nullable = false)
    private Lawyer lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_buffet", nullable = false)
    private Buffet buffet;
}
