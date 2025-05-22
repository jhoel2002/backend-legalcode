package com.application.claimhereweb.service;

import java.util.List;

import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.StatusCaseRequestDTO;

public interface ICaseRequestService {

    public ResponseCaseRequestDTO saveCaseRequest(SaveCaseRequestDTO SaveCaseDTO, Long id_customer);

    public ResponseCaseDTO statusCaseRequest(StatusCaseRequestDTO updateCaseRequestDTO);

    public List<ResponseCaseRequestDTO> findAll(String status);
}
