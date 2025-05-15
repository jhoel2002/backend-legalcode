package com.application.claimhereweb.service;

import org.springframework.http.ResponseEntity;

import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateCaseRequestDTO;

public interface ICaseRequestService {

    public ResponseCaseRequestDTO saveCaseRequest(SaveCaseRequestDTO SaveCaseDTO, Long id_customer);

    public ResponseEntity<String> updateCaseRequest(UpdateCaseRequestDTO updateCaseRequestDTO);
}
