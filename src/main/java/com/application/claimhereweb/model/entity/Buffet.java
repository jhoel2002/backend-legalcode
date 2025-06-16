package com.application.claimhereweb.model.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "buffet")
public class Buffet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "encrypted", nullable = false)
    private String encrypted;

    @Column(name = "creation")
    @CreationTimestamp
    private Timestamp creation;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "img")
    private String img;

    @Column(name = "description")
    private String description;

    @Column(name = "latitud")
    private String latitud;

    @Column(name = "longitud")
    private String longitud;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = TypeCase.class)
    @JoinTable(name = "buffet_type_case", joinColumns = @JoinColumn(name = "id_buffet"), inverseJoinColumns = @JoinColumn(name = "id_type_case"))
    private Set<TypeCase> typeCase = new HashSet<>();
}