package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseUserDTO {
    private Long id;
    private String code;
    private String name;
    private String last_name;
    private String email;
    private String phone;
    private String role;
    private String address;
    private String creation;
    private Boolean enabled;
}
