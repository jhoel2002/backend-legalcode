package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseLawyerSimpleDTO {
    private String busqueda;

    public ResponseLawyerSimpleDTO(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }
}
