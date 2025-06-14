package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCustomerDTO {
    private Long id;
    private String code;
    private String fullName;
    private String document_type;
    private String document_number;
    private String email;
    private String phone;
    private String address;
    private Boolean enable;
    private String creation;
    private String buffet;
}
