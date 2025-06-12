package com.application.claimhereweb.service;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.ResponseCustomerDTO;
import com.application.claimhereweb.service.dto.ResponseSaveCustomerDTO;
import com.application.claimhereweb.service.dto.SaveCustomerDTO;
import com.application.claimhereweb.service.dto.UpdateCustomerDTO;

import java.sql.Timestamp;

import org.springframework.data.domain.Pageable;

public interface ICustomerService {

        // Metodo para guardar cliente
        public ResponseSaveCustomerDTO saveCustomer(SaveCustomerDTO saveCustomerDTO);

        // Metodo para actualizar los datos del cliente
        public ResponseSaveCustomerDTO updateCustomer(String code, UpdateCustomerDTO dto);

        public SimplePageResponse<ResponseCustomerDTO> listFilterSearch(String search, Pageable pageable);

        public SimplePageResponse<ResponseCustomerDTO> findAll(Pageable pageable);

        public SimplePageResponse<ResponseCustomerDTO> findAllByCreationDate(Timestamp starDate, Timestamp endDate,
                        Pageable pageable);

        public SimplePageResponse<ResponseCustomerDTO> listFilterSearchAndDate(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable);
}