package com.application.claimhereweb.model.entity;

import com.application.claimhereweb.model.entity.enumEntity.DocumentCustomertype;

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
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type_document_customer", nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentCustomertype type_document_customer;

    @Column(name = "document", nullable = false)
    private String document;

    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
}