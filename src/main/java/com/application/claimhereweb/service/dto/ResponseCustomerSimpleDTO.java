package com.application.claimhereweb.service.dto;

import lombok.Data;

@Data
public class ResponseCustomerSimpleDTO {
    private String busqueda;

    public ResponseCustomerSimpleDTO(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }
}
