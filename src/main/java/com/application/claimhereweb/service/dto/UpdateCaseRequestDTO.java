package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class UpdateCaseRequestDTO {
    private String title;
    private String description;
    private String type_case;
}