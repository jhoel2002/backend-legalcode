package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class SaveFactureDTO {

    private Double amount;
    private String description;
    private Long legalCase;
    private Long user;
    // private Long customerId; Se traera el customer del legalCaseId
}