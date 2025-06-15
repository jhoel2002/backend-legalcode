package com.application.claimhereweb.service;

import java.sql.Timestamp;

//import java.util.List;

//import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.AssignLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateStatusCaseRequestDTO;

public interface ICaseRequestService {

        public ResponseCaseRequestDTO saveEvidenceMassive(SaveCaseRequestDTO dto, String codeCustomer,
                        MultipartFile[] files);

        public ResponseCaseRequestDTO save(SaveCaseRequestDTO dto, String codeCustomer, MultipartFile file);

        public void updateStatusCaseRequest(UpdateStatusCaseRequestDTO dto);

        public ResponseCaseRequestDTO updateInfo(UpdateCaseRequestDTO dto, String code);

        public void assignLawyerCase(AssignLawyerDTO dto);

        public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(String status, Pageable pageable,
                        String codeBuffet);

        public SimplePageResponse<ResponseCaseRequestDTO> findAll(Pageable pageable, String code);

        public SimplePageResponse<ResponseCaseRequestDTO> listFilterSearch(String search, Pageable pageable,
                        String codeBuffet, String status);

        public SimplePageResponse<ResponseCaseRequestDTO> findAllbyApplicationDate(Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable,
                        String codeBuffet,
                        String status);

        public SimplePageResponse<ResponseCaseRequestDTO> listFilterFull(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        String status,
                        Pageable pageable,
                        String codeBuffet);

        public ResponseCaseRequestDTO saveEvidenceMassiveQuotation(SaveCaseRequestDTO dto, String codeCustomer,
                        MultipartFile[] evidencia, MultipartFile[] cotizacion);
}