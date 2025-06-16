package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseLawyerEnableDTO {
    private String code;
    private String name;
    private String case_type;
    private String description;
    private String img;
}