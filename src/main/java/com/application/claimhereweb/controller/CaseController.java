package com.application.claimhereweb.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyer;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
//import com.application.claimhereweb.model.entity.Case;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
import com.application.claimhereweb.service.dto.UpdateLawyer;
//import com.application.claimhereweb.service.dto.SaveCaseUserDTO;
import com.application.claimhereweb.service.impl.CaseServiceImpl;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/cases")
public class CaseController {

    @Autowired
    private CaseServiceImpl caseService;

    @PutMapping("/assignLawyer")
    public ResponseEntity<?> assignLawyer(@Valid @RequestBody UpdateLawyer updateLawyer) {
        ReponseUpdateLawyer reponseUpdateLawyer = caseService.assignLawyer(updateLawyer);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponseUpdateLawyer);
    }

    @GetMapping
    public SimplePageResponse<ResponseCaseDTO> listAll(Pageable pageable) {
        return caseService.findAll(pageable);
    }

    @GetMapping("/listFilterStartDate")
    public SimplePageResponse<ResponseCaseDTO> listFilterStartDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return caseService.findAllByStartDate(startTimestamp, endTimestamp, pageable);
        }

        return caseService.findAll(pageable);
    }

    @GetMapping("/listFilterSearch")
    public SimplePageResponse<ResponseCaseDTO> listFilterSearch(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return caseService.listFilterSearch(search, pageable);
    }

    @GetMapping("/listFilterFull")
    public SimplePageResponse<ResponseCaseDTO> listFilterFull(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));
        }

        return caseService.listFilterSearchAndDate(search, startTimestamp, endTimestamp, pageable);
    }

    @PostMapping("/registerCase/administrator/cust/{id_customer}")
    // @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> createCase(@Valid @RequestBody SaveCaseDTO value_case, @PathVariable Long id_customer) {
        ResponseCaseDTO response = caseService.saveCaseAdministrator(value_case, id_customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
     * @PostMapping("/registerCase/customer/cust")
     * // @PreAuthorize("hasRole('ROLE_CUSTOMER')")
     * public ResponseEntity<?> createCase(@Valid @RequestBody SaveCaseUserDTO
     * value_case) {
     * ResponseCaseDTO response = caseService.saveCaseUser(value_case);
     * return ResponseEntity.status(HttpStatus.CREATED).body(response);
     * }
     */
}