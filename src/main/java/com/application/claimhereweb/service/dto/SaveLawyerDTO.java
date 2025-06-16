package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class SaveLawyerDTO {
    private String email;
    private String name;
    private String password;
    private String last_name;
    private String phone;
    private String address;
    private String case_type;
    private String description;
}
