package com.application.claimhereweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.application.claimhereweb.service.dto.ReponseSaveBuffetDTO;
import com.application.claimhereweb.service.dto.SaveBuffetDTO;
import com.application.claimhereweb.service.dto.UpdateBuffetEnableDTO;
import com.application.claimhereweb.service.impl.BuffetServiceImpl;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/buffet")
public class BuffetController {
    @Autowired
    private BuffetServiceImpl buffetService;

    @PostMapping("/save")
    public ResponseEntity<ReponseSaveBuffetDTO> createBuffet(@Valid @RequestBody SaveBuffetDTO dto) {
        ReponseSaveBuffetDTO response = buffetService.saveBuffet(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/enable")
    public ResponseEntity<Void> updateBuffetEnable(@Valid @RequestBody UpdateBuffetEnableDTO dto) {
        buffetService.updateBuffetEnableStatus(dto.getCode(), dto.isEnable());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{code}/logo")
    public ResponseEntity<String> uploadBuffetLogo(@PathVariable String code,
            @RequestParam("file") MultipartFile file) {
        String logoUrl = buffetService.uploadBuffetLogo(file, code);
        return ResponseEntity.ok(logoUrl);
    }

    @PostMapping("/saveWithLogo")
    public ResponseEntity<ReponseSaveBuffetDTO> registerBuffetWithLogo(
            @RequestParam("name") String name,
            @RequestParam("enable") boolean enable,
            @RequestParam("file") MultipartFile logoFile) {

        SaveBuffetDTO dto = new SaveBuffetDTO();
        dto.setName(name);
        dto.setEnable(enable);

        ReponseSaveBuffetDTO response = buffetService.saveBuffetWithLogo(dto, logoFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}