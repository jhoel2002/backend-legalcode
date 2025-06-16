package com.application.claimhereweb.service.dto;

import com.application.claimhereweb.model.entity.enumEntity.CaseType;

import lombok.Data;

@Data
public class SaveCaseRequestDTO {

    private String title;
    private String description;
    private CaseType type_case;
    private String lawyer;
}
