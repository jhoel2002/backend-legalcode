package com.application.claimhereweb.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.ILawyerService;
import com.application.claimhereweb.service.dto.ResponseLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseSaveLawyerDTO;
import com.application.claimhereweb.service.dto.SaveLawyerDTO;
import com.application.claimhereweb.service.dto.UpdateLawyerDTO;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/lawyer")
public class LawyerController {

    @Autowired
    private ILawyerService lawyerService;

    @PostMapping("/save")
    public ResponseEntity<ResponseSaveLawyerDTO> saveLawyer(@Valid @RequestBody SaveLawyerDTO dto) {
        ResponseSaveLawyerDTO response = lawyerService.saveLawyer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/updateLawyer/{code}")
    public ResponseEntity<ResponseSaveLawyerDTO> updateLawyer(
            @PathVariable String code,
            @Valid @RequestBody UpdateLawyerDTO dto) {

        ResponseSaveLawyerDTO response = lawyerService.updateLawyer(code, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public SimplePageResponse<ResponseLawyerDTO> listAll(Pageable pageable) {
        return lawyerService.findAll(pageable);
    }

    @GetMapping("/listFilterFull")
    public SimplePageResponse<ResponseLawyerDTO> listFilterFull(
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

        return lawyerService.listFilterSearchAndDate(search, startTimestamp, endTimestamp, pageable);
    }

    @GetMapping("/listFilterSearch")
    public SimplePageResponse<ResponseLawyerDTO> listFilterSearch(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return lawyerService.listFilterSearch(search, pageable);
    }

    @GetMapping("/listFilterCreationDate")
    public SimplePageResponse<ResponseLawyerDTO> listFilterCreationDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return lawyerService.findAllByCreationDate(startTimestamp, endTimestamp, pageable);
        }

        return lawyerService.findAll(pageable);
    }
}
