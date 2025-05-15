package com.application.claimhereweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.claimhereweb.service.dto.ResponseCaseDTO;
//import com.application.claimhereweb.model.entity.Case;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
import com.application.claimhereweb.service.dto.SaveCaseUserDTO;
import com.application.claimhereweb.service.impl.CaseServiceImpl;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/cases")
public class CaseController {

    @Autowired
    private CaseServiceImpl caseService;

    @PostMapping("/registerCase/administrator/cust/{id_customer}")
    // @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<?> createCase(@Valid @RequestBody SaveCaseDTO value_case, @PathVariable Long id_customer) {
        ResponseCaseDTO response = caseService.saveCaseAdministrator(value_case, id_customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/registerCase/customer/cust")
    // @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> createCase(@Valid @RequestBody SaveCaseUserDTO value_case) {
        ResponseCaseDTO response = caseService.saveCaseUser(value_case);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}