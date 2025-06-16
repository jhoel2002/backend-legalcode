package com.application.claimhereweb.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ReponseSaveBuffetDTO {
    private Long id;
    private String name;
    private String description;
    private String longitud;
    private String latitud;
    private String code;
    private boolean enable;
    private List<String> typeCase;
}
