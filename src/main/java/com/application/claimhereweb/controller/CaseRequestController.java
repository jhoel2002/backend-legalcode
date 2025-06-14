package com.application.claimhereweb.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.AssignLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateStatusCaseRequestDTO;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/casesRequest")
public class CaseRequestController {

    @Autowired
    private ICaseRequestService caseRequestService;

    @PostMapping("/saveEvidenceMassive/{codeCustomer}")
    public ResponseEntity<ResponseCaseRequestDTO> saveCaseWithEvidence(
            @PathVariable("codeCustomer") String codeCustomer,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type_case") CaseType typeCase) {

        SaveCaseRequestDTO dto = new SaveCaseRequestDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setType_case(typeCase);

        ResponseCaseRequestDTO response = caseRequestService.saveEvidenceMassive(dto, codeCustomer, files);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/save/{codeCustomer}")
    public ResponseEntity<ResponseCaseRequestDTO> saveCaseWithEvidence(
            @PathVariable("codeCustomer") String codeCustomer,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type_case") String typeCase) {

        SaveCaseRequestDTO dto = new SaveCaseRequestDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setType_case(CaseType.valueOf(typeCase));

        ResponseCaseRequestDTO response = caseRequestService.save(dto, codeCustomer, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/updateInfo/{code}")
    public ResponseEntity<ResponseCaseRequestDTO> updateInfo(
            @PathVariable String code,
            @Valid @RequestBody UpdateCaseRequestDTO dto) {

        ResponseCaseRequestDTO response = caseRequestService.updateInfo(dto, code);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateStatus")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody UpdateStatusCaseRequestDTO dto) {
        caseRequestService.updateStatusCaseRequest(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/assignLawyer")
    public ResponseEntity<Void> assignLawyer(@Valid @RequestBody AssignLawyerDTO dto) {
        caseRequestService.assignLawyerCase(dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codeBuffet}")
    public SimplePageResponse<ResponseCaseRequestDTO> listAll(Pageable pageable,
            @PathVariable String codeBuffet) {

        return caseRequestService.findAll(pageable, codeBuffet);
    }

    @GetMapping("/listFilterStatus/{codeBuffet}")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(
            @RequestParam(required = false) String status,
            Pageable pageable,
            @PathVariable String codeBuffet) {
        return caseRequestService.listFilterStatus(status, pageable, codeBuffet);
    }

    @GetMapping("/listFilterSearch/{codeBuffet}")
    public SimplePageResponse<ResponseCaseRequestDTO> listerFilterSearch(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            Pageable pageable,
            @PathVariable String codeBuffet) {
        return caseRequestService.listFilterSearch(search, pageable, codeBuffet, status);
    }

    @GetMapping("/listFilterApplicationDate/{codeBuffet}")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterApplicationDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable,
            @PathVariable String codeBuffet,
            @RequestParam String status) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return caseRequestService.findAllbyApplicationDate(startTimestamp,
                    endTimestamp, pageable, codeBuffet, status);
        }

        return caseRequestService.listFilterStatus(status, pageable, codeBuffet);
    }

    @GetMapping("/listFilterFull/{codeBuffet}")
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterFull(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable,
            @PathVariable String codeBuffet) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Timestamp startTimestamp = null;
        Timestamp endTimestamp = null;

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);
            startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return caseRequestService.listFilterFull(search, startTimestamp,
                    endTimestamp, status, pageable, codeBuffet);
        }
        return caseRequestService.findAll(pageable, codeBuffet);
    }
}