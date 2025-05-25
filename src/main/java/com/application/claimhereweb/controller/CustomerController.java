package com.application.claimhereweb.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.service.ICustomerService;
import com.application.claimhereweb.service.dto.ResponseCustomerDTO;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/customer")
public class CustomerController {

    @Autowired
    private ICustomerService customerService;

    @GetMapping
    public SimplePageResponse<ResponseCustomerDTO> listAll(Pageable pageable) {
        return customerService.findAll(pageable);
    }

    @GetMapping("/listFilterSearch")
    public SimplePageResponse<ResponseCustomerDTO> listFilterSearch(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return customerService.listFilterSearch(search, pageable);
    }

    @GetMapping("/listFilterCreationDate")
    public SimplePageResponse<ResponseCustomerDTO> listFilterCreationDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        if (startDate != null && endDate != null) {
            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            Timestamp startTimestamp = Timestamp.valueOf(startLocalDate.atStartOfDay());
            Timestamp endTimestamp = Timestamp.valueOf(endLocalDate.atTime(LocalTime.MAX));

            return customerService.findAllByCreationDate(startTimestamp, endTimestamp, pageable);
        }

        return customerService.findAll(pageable);
    }

    @GetMapping("/listFilterFull")
    public SimplePageResponse<ResponseCustomerDTO> listFilterFull(
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

        return customerService.listFilterSearchAndDate(search, startTimestamp, endTimestamp, pageable);
    }
}