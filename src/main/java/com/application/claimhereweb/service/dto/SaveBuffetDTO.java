package com.application.claimhereweb.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class SaveBuffetDTO {
    private String name;
    private boolean enable;
    private String description;
    private String latitud;
    private String longitud;
    private List<String> typeCase;
}
