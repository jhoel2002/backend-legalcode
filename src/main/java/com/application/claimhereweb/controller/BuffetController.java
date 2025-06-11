package com.application.claimhereweb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.application.claimhereweb.service.dto.ReponseSaveBuffetDTO;
import com.application.claimhereweb.service.dto.SaveBuffetDTO;
import com.application.claimhereweb.service.impl.BuffetServiceImpl;
import com.application.claimhereweb.service.impl.CaseRequestImpl;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/buffet")
public class BuffetController {
    @Autowired
    private BuffetServiceImpl buffetServiceImpl;
    private static final Logger logger = LoggerFactory.getLogger(CaseRequestImpl.class);

    @PostMapping("/saveBuffet")
    public ResponseEntity<ReponseSaveBuffetDTO> saveBuffet(@Valid @RequestBody SaveBuffetDTO dto) {
        logger.info("Guardando buffet con name='{}', enable='{}'", dto.getName(), dto.isEnable());

        ReponseSaveBuffetDTO response = buffetServiceImpl.saveBuffet(dto);
        return ResponseEntity.ok(response);
    }
}
