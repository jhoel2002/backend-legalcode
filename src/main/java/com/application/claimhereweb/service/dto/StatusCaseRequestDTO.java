package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class StatusCaseRequestDTO {
    private Long id;
    private String status_request;
    private Long user;
}
