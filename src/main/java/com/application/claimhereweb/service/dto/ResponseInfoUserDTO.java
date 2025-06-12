package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseInfoUserDTO {
    private String code;
    private String buffet;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String document_type;
    private String document_number;
}
