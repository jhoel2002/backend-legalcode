package com.application.claimhereweb.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ReponseCaseRequestCarryDocument {
    private String code;
    private List<Map<String, String>> documentMap;
}