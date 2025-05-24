package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCustomerDTO {
    private Long id;
    private String type_document_customer;
    private String document;
    private Long userId;
    private String email;
    private String name;
    private String last_name;
    private String phone;
    private Boolean enabled;
}
