package com.application.claimhereweb.service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ResponseCaseRequestInfoDTO {
    private String title;
    private String code;
    private String type_case;
    private String status_request;
    private String creation;
    private String lawyerName; // Puede ser null si aún no se asigna
    private String description;

    // Información del cliente
    private String customerName;
    private String customerEmail;
    private String customerDocumentType;
    private String customerDocumentNumber;

    // Lista de URLs o rutas de evidencias
    private List<Map<String, String>> evidencias;
    private Map<String, String> cotizacion;
}