package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class UploadedDocumentInfo {
    private final String code;
    private final String originalName;

    public UploadedDocumentInfo(String code, String originalName) {
        this.code = code;
        this.originalName = originalName;
    }

    public String getCode() {
        return code;
    }

    public String getOriginalName() {
        return originalName;
    }

}
