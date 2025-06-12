package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseLawyerDTO {
    private Long id;
    private String code;
    private String fullName;
    private String case_type;
    private String email;
    private String phone;
    private String address;
    private Boolean enabled;
    private String creation;
    private String buffet;
}
