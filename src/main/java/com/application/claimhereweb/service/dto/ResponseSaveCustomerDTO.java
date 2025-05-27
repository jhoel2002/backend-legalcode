package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseSaveCustomerDTO {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String last_name;
    private String phone;
    private String address;
    private String type_document_customer;
    private String document;
}
