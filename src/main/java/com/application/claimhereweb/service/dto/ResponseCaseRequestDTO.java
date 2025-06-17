package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCaseRequestDTO {

    private Long id;
    private String code;
    private String title;
    private String description;
    private String type_case;
    private String status_request;
    private String customer;
    private String creation;
    private String buffet;
    private String lawyer;
}
