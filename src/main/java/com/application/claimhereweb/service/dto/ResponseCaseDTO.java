package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCaseDTO {

    private Long id;
    private String title;
    private String description;
    private String type_case;
    private String status_case;
    private String customer;
    private String start_date;
}
