package com.application.claimhereweb.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerEnableDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerSimpleDTO;
import com.application.claimhereweb.service.dto.ResponseSaveLawyerDTO;
import com.application.claimhereweb.service.dto.SaveLawyerDTO;
import com.application.claimhereweb.service.dto.UpdateLawyerDTO;

public interface ILawyerService {

        public List<ResponseLawyerEnableDTO> getEnabledLawyers();

        public ResponseSaveLawyerDTO saveLawyer(SaveLawyerDTO dto, String codeBuffet, MultipartFile[] foto);

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

        public List<ResponseLawyerSimpleDTO> searchSimpleLawyer(String search, String codeBuffet, CaseType typeCase);
}
