package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ReponseUpdateLawyerDTO {
    private Long id;
    private String email;
    private String name;
    private String code;
    private String password;
    private String last_name;
    private String phone;
    private String address;
    private String buffet;
}
