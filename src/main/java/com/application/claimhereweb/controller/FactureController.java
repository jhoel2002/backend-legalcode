package com.application.claimhereweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.claimhereweb.service.IFactureService;
import com.application.claimhereweb.service.dto.ResponseFactureDTO;
import com.application.claimhereweb.service.dto.SaveFactureDTO;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/factures")
public class FactureController {

    @Autowired
    private IFactureService factureService;

    @PostMapping("/registerFacture/cust/{id_customer}")
    // @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> createCase(@Valid @RequestBody SaveFactureDTO value_case,
            @PathVariable Long id_customer) {
        ResponseFactureDTO response = factureService.saveFacture(value_case, id_customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
