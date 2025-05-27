package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseStatusUpdateCase {
    private Long id;
    private String title;
    private String status_case;
    private String type_case;
}
