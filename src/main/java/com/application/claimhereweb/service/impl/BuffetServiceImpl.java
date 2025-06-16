package com.application.claimhereweb.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.TypeCase;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.TypeCaseRepository;
import com.application.claimhereweb.service.IBuffetService;
import com.application.claimhereweb.service.dto.ReponseSaveBuffetDTO;
import com.application.claimhereweb.service.dto.SaveBuffetDTO;
import org.springframework.http.HttpStatus;

@Service
public class BuffetServiceImpl implements IBuffetService {
    private static final Logger logger = LoggerFactory.getLogger(CaseRequestImpl.class);

    @Autowired
    BuffetRepository buffetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TypeCaseRepository typeCaseRepository;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public ReponseSaveBuffetDTO saveBuffetWithLogo(SaveBuffetDTO dto, MultipartFile file) {
        logger.info("Verificando si el buffet '{}' ya existe...", dto.getName());

        if (buffetRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("El buffet con nombre '" + dto.getName() + "' ya está registrado.");
        }

        Buffet buffet = modelMapper.map(dto, Buffet.class);
        String codigoBuffet = generateUniqueCode();
        buffet.setCode(codigoBuffet);
        buffet.setEncrypted(passwordEncoder.encode(codigoBuffet));

        if (file != null && !file.isEmpty()) {
            String mimeType = file.getContentType();
            List<String> allowedMimeTypes = List.of("image/png", "image/jpeg", "image/jpg");

            if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                throw new IllegalArgumentException("Tipo de archivo no permitido: " + mimeType);
            }

            try {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String originalName = file.getOriginalFilename();
                String newFileName = timestamp + "_" + originalName;

                String s3Key = "buffets/" + codigoBuffet + "/logo/" + newFileName;

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                metadata.setContentType(file.getContentType());

                amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata));

                String s3Url = amazonS3.getUrl(bucketName, s3Key).toString();
                buffet.setImg(s3Url);

            } catch (IOException e) {
                logger.error("Error al subir logo a S3: {}", e.getMessage(), e);
                throw new RuntimeException("Fallo al subir el logo a S3", e);
            }
        }

        Set<TypeCase> typeCases = dto.getTypeCase().stream()
                .map(name -> typeCaseRepository.findByName(name)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Tipo de Caso no encontrado: " + name)))
                .collect(Collectors.toSet());

        buffet.setTypeCase(typeCases);

        buffet = buffetRepository.save(buffet);

        ReponseSaveBuffetDTO response = modelMapper.map(buffet, ReponseSaveBuffetDTO.class);

        List<String> typeCaseNames = buffet.getTypeCase().stream()
                .map(TypeCase::getName)
                .collect(Collectors.toList());

        response.setTypeCase(typeCaseNames);

        return response;
    }

    @Override
    @Transactional
    public ReponseSaveBuffetDTO saveBuffet(SaveBuffetDTO dto) {
        logger.info("Verificando si el buffet '{}' ya existe...", dto.getName());

        if (buffetRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("El buffet con nombre '" + dto.getName() + "' ya está registrado.");
        }

        Buffet buffet = modelMapper.map(dto, Buffet.class);

        String codigoBuffet = generateUniqueCode();
        buffet.setCode(codigoBuffet);

        buffet.setEncrypted(passwordEncoder.encode(codigoBuffet));

        buffet.setImg(null);

        buffet = buffetRepository.save(buffet);

        ReponseSaveBuffetDTO response = modelMapper.map(buffet, ReponseSaveBuffetDTO.class);
        return response;
    }

    @Override
    @Transactional
    public String uploadBuffetLogo(MultipartFile file, String buffetCode) {
        String mimeType = file.getContentType();

        List<String> allowedMimeTypes = List.of("image/png", "image/jpeg", "image/jpg");

        if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + mimeType);
        }

        Buffet buffet = buffetRepository.findByCode(buffetCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buffet no encontrado"));

        File mainFile = new File(file.getOriginalFilename());
        try (FileOutputStream stream = new FileOutputStream(mainFile)) {
            stream.write(file.getBytes());

            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalName = mainFile.getName();
            String newFileName = timestamp + "_" + originalName;

            String s3Key = "buffets/" + buffetCode + "/logo/" + newFileName;

            PutObjectRequest request = new PutObjectRequest(bucketName, s3Key, mainFile);
            amazonS3.putObject(request);

            String s3Url = amazonS3.getUrl(bucketName, s3Key).toString();

            buffet.setImg(s3Url);
            buffetRepository.save(buffet);

            return s3Url;

        } catch (IOException e) {
            logger.error("Error al subir logo a S3: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo al subir el logo a S3", e);
        } finally {
            if (mainFile.exists()) {
                mainFile.delete();
            }
        }
    }

    @Override
    @Transactional
    public void updateBuffetEnableStatus(String code, boolean enable) {
        Buffet buffet = buffetRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buffet no encontrado"));

        buffet.setEnable(enable);
        buffetRepository.save(buffet);
    }

    @Override
    @Transactional
    public List<String> getTypeCasesByBuffetCode(String code) {
        Buffet buffet = buffetRepository.findByCodeWithTypeCases(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Buffet no tiene asociado ningun tipo de caso legal: " + code));

        return buffet.getTypeCase().stream()
                .map(TypeCase::getName)
                .collect(Collectors.toList());
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
