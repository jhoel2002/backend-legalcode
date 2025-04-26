package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.application.claimhereweb.model.entity.enumEntity.StatusPayment;

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
@Table(name = "facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "issue_date", nullable = false)
    @CreationTimestamp
    private Timestamp issue_date;

    @Enumerated(EnumType.STRING)
    private StatusPayment status_payment = StatusPayment.PENDIENTE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_legal_case", referencedColumnName = "id", nullable = false)
    private LegalCase legal_case;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_customer", referencedColumnName = "id", nullable = true)
    private Customer customer;
}
