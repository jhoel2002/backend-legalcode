package com.application.claimhereweb.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.StatusCaseRequestDTO;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/casesRequest")
public class CaseRequestController {

    @Autowired
    private ICaseRequestService caseRequestService;

    @GetMapping
    public SimplePageResponse<ResponseCaseRequestDTO> listAll(Pageable pageable) {
        return caseRequestService.findAll(pageable);
    }

    @GetMapping("/listFilterStatus")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return caseRequestService.listFilterStatus(status, pageable);
    }

    @GetMapping("/listFilterSearch")
    public SimplePageResponse<ResponseCaseRequestDTO> listerFilterSearch(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return caseRequestService.listFilterSearch(search, pageable);
    }

    @GetMapping("/listFilterApplicationDate")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterApplicationDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return caseRequestService.findAllbyApplicationDate(startTimestamp, endTimestamp, pageable);
        }

        return caseRequestService.findAll(pageable);
    }

    @GetMapping("/listFilterFull")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterFull(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
            startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return caseRequestService.listFilterFull(search, startTimestamp, endTimestamp, status, pageable);
        }
        return caseRequestService.findAll(pageable);
    }

    @PostMapping("/registerCaseRequest/cust/{id_customer}")
    // @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> createCase(@Valid @RequestBody SaveCaseRequestDTO value_case,
            @PathVariable Long id_customer) {
        ResponseCaseRequestDTO response = caseRequestService.saveCaseRequest(value_case, id_customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/statusCaseRequest")
    // @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseCaseDTO statusCaseRequest(@Valid @RequestBody StatusCaseRequestDTO value_case) {
        return caseRequestService.statusCaseRequest(value_case);
    }
}
