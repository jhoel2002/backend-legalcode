package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.EventType;

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
@Table(name = "agenda")
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    @CreationTimestamp
    private Timestamp start_date;

    @Column(name = "end_date", nullable = false)
    @CreationTimestamp
    private Timestamp end_date;

    @Column(name = "type_event", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType type_event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_legal_case", referencedColumnName = "id", nullable = false)
    private LegalCase legal_case;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = true)
    private User user;
}