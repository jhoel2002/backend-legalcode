package com.application.claimhereweb.service;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyer;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.ResponseStatusUpdateCase;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
import com.application.claimhereweb.service.dto.UpdateLawyer;
import com.application.claimhereweb.service.dto.UpdateStatusCase;

import java.sql.Timestamp;

//import com.application.claimhereweb.service.dto.SaveCaseUserDTO;
import org.springframework.data.domain.Pageable;

public interface ICaseService {

        public ResponseStatusUpdateCase statusCase(UpdateStatusCase updateStatusCase);

        public ReponseUpdateLawyer assignLawyer(UpdateLawyer updateLawyer);

        public ResponseCaseDTO saveCaseAdministrator(SaveCaseDTO value_case, Long id_customer);

        public SimplePageResponse<ResponseCaseDTO> findAll(Pageable pageable);

        public SimplePageResponse<ResponseCaseDTO> findAllByStartDate(Timestamp startDate, Timestamp endDate,
                        Pageable pageable);

        public SimplePageResponse<ResponseCaseDTO> listFilterSearch(String search, Pageable pageable);

        public SimplePageResponse<ResponseCaseDTO> listFilterSearchAndDate(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable);
        // public ResponseCaseDTO saveCaseUser(SaveCaseUserDTO value_case);
}