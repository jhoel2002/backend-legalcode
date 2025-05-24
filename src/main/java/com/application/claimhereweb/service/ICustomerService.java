package com.application.claimhereweb.service;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.ResponseCustomerDTO;

import java.sql.Timestamp;

import org.springframework.data.domain.Pageable;

public interface ICustomerService {

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
