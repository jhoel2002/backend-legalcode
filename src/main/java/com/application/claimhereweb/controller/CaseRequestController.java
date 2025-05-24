package com.application.claimhereweb.controller;

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
    public SimplePageResponse<ResponseCaseRequestDTO> listAll(
            @RequestParam(required = false) String status,
            Pageable pageable) {

        if (status == null || status.equalsIgnoreCase("ALL")) {
            return caseRequestService.findAll(pageable);
        } else {
            return caseRequestService.findAllFilter(status, pageable);
        }
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
