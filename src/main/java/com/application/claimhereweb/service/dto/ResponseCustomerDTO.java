package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCustomerDTO {
    private Long id;
    private String type_document_customer;
    private String document;
    private String email;
    private String name;
    private String last_name;
    private String phone;
    private String address;
    private Boolean enabled;
    private String creation;
}
