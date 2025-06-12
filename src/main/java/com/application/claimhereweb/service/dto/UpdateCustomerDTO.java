package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class UpdateCustomerDTO {
    private String email;
    private String name;
    private String password;
    private String last_name;
    private String phone;
    private String address;
    private String document_type;
    private String document_number;
}