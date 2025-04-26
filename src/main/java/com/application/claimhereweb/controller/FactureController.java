package com.application.claimhereweb.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

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

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf() throws JRException, FileNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("Facture", "Facture.pdf");
        return ResponseEntity.ok().headers(headers).body(factureService.exportPdf());
    }

    @GetMapping("/export-xls")
    public ResponseEntity<byte[]> exportXls() throws JRException, FileNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
        var contentDisposition = ContentDisposition.builder("attachment")
                .filename("petsReport" + ".xls").build();
        headers.setContentDisposition(contentDisposition);
        return ResponseEntity.ok()
                .headers(headers)
                .body(factureService.exportXls());
    }
}
