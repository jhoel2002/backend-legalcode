package com.application.claimhereweb.service.impl;

import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.service.IBuffetService;
import com.application.claimhereweb.service.dto.ReponseSaveBuffetDTO;
import com.application.claimhereweb.service.dto.SaveBuffetDTO;

@Service
public class BuffetServiceImpl implements IBuffetService {
    private static final Logger logger = LoggerFactory.getLogger(CaseRequestImpl.class);

    @Autowired
    BuffetRepository buffetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override

    @Transactional
    public ReponseSaveBuffetDTO saveBuffet(SaveBuffetDTO dto) {
        logger.info("Verificando si el buffet '{}' ya existe...", dto.getName());
        if (buffetRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("El buffet con nombre '" + dto.getName() + "' ya está registrado.");
        }
        Buffet buffet = modelMapper.map(dto, Buffet.class);
        buffet.setCode(generateUniqueCode());
        buffet = buffetRepository.save(buffet);
        ReponseSaveBuffetDTO response = modelMapper.map(buffet, ReponseSaveBuffetDTO.class);
        return response;
    }

    private String generateCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++)
            code.append(letters.charAt(random.nextInt(letters.length())));
        for (int i = 0; i < 3; i++)
            code.append(random.nextInt(10));
        return code.toString();
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (buffetRepository.existsByCode(code));
        return code;
    }
}
