package com.application.claimhereweb.model.entity;

import java.sql.Date;

import com.application.claimhereweb.model.entity.enumEntity.CaseRole;

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
@Table(name = "case_team_user")
public class CaseTeamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_legal_case", referencedColumnName = "id", nullable = false)
    private LegalCase legal_case;

    @ManyToOne
    @JoinColumn(name = "id_users", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "role_case", nullable = false)
    @Enumerated(EnumType.STRING)
    private CaseRole role_case;

    @JoinColumn(name = "asignation_date", nullable = false)
    private Date asignation_date;
}
