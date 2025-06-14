package com.application.claimhereweb.service;

import java.sql.Timestamp;

import org.springframework.data.domain.Pageable;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseSaveLawyerDTO;
import com.application.claimhereweb.service.dto.SaveLawyerDTO;
import com.application.claimhereweb.service.dto.UpdateLawyerDTO;

public interface ILawyerService {

        public ResponseSaveLawyerDTO saveLawyer(SaveLawyerDTO dto);

        public ReponseUpdateLawyerDTO updateLawyer(String code, UpdateLawyerDTO dto);

        public SimplePageResponse<ResponseLawyerDTO> listFilterSearch(String search, Pageable pageable,
                        String codeBuffet);

        public SimplePageResponse<ResponseLawyerDTO> findAll(Pageable pageable, String codeBuffet);

        public SimplePageResponse<ResponseLawyerDTO> findAllByCreationDate(Timestamp startDate, Timestamp endDate,
                        Pageable pageable,
                        String codeBuffet);

        public SimplePageResponse<ResponseLawyerDTO> listFilterSearchAndDate(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable,
                        String codeBuffet);
}
