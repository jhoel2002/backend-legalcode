package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseFactureDTO {
    private Long id;
    private Double amount;;
    private String description;

    private String issue_date;
    private String status_payment;
    // private String legal_case; // Título del caso legal
    // private String user; Usuario con rol Coordinador
    private String customer; // Nombre del cliente
}
