package com.application.claimhereweb.model.entity;

import java.sql.Date;

import com.application.claimhereweb.model.entity.enumEntity.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "read_message", nullable = false)
    private boolean read_message = false;

    @Column(name = "type_message", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type_message;

    @Column(name = "shipping_date", nullable = false)
    private Date shipping_date;

    @ManyToOne
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = false)
    private User user;
}
