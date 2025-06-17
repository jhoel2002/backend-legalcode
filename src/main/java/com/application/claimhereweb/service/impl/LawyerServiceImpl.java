package com.application.claimhereweb.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.Lawyer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.LawyerRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ILawyerService;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerEnableDTO;
import com.application.claimhereweb.service.dto.ResponseLawyerSimpleDTO;
import com.application.claimhereweb.service.dto.ResponseSaveLawyerDTO;
import com.application.claimhereweb.service.dto.SaveLawyerDTO;
import com.application.claimhereweb.service.dto.UpdateLawyerDTO;

@Service
public class LawyerServiceImpl implements ILawyerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    LawyerRepository lawyerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BuffetRepository buffetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional
    public List<ResponseLawyerEnableDTO> getEnabledLawyers() {
        List<Lawyer> lawyers = lawyerRepository.findEnabledLawyers();

        return lawyers.stream().map(lawyer -> {
            ResponseLawyerEnableDTO dto = new ResponseLawyerEnableDTO();
            dto.setCode(lawyer.getUser().getCode());
            dto.setName(lawyer.getUser().getName() + " " + lawyer.getUser().getLast_name());
            dto.setCase_type(lawyer.getCase_type().toString());
            dto.setDescription(lawyer.getDescription());
            dto.setImg(lawyer.getImg());
            return dto;
        }).toList();
    }

    @Override
    @Transactional

    public ResponseSaveLawyerDTO saveLawyer(SaveLawyerDTO dto, String codeBuffet, MultipartFile[] foto) {

        if (foto != null && foto.length > 1) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Solo se permite un archivo de foto.");
        }
        List<String> allowedMimeTypes = List.of(
                "image/png", "image/jpeg", "image/jpg");

        if (foto != null && foto.length == 1) {
            MultipartFile archivo = foto[0];
            String mimeType = archivo.getContentType();
            if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Tipo de archivo no permitido: " + mimeType);
            }
        }

        logger.info("Verificando buffet con ID {}", codeBuffet);

        Buffet buffet = buffetRepository.findByCode(codeBuffet)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buffet no encontrado"));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnable(true);
        user.setCode(generateUniqueCode());
        user.setBuffet(buffet);
        user = userRepository.save(user);

        logger.info("Asignando al usuario el rol de abogado");
        Long role = 2L; // (Role 2 = Lawyer)
        roleRepository.assignRoleToUser(user.getId(), role);

        Lawyer lawyer = modelMapper.map(dto, Lawyer.class);
        lawyer.setUser(user);

        if (foto != null && foto.length == 1) {
            MultipartFile archivo = foto[0];
            if (!archivo.isEmpty()) {
                String s3Key = uploadAndSaveDocument(archivo, user.getCode());
                lawyer.setImg(s3Key);
                lawyer = lawyerRepository.save(lawyer);
            }
        }

        ResponseSaveLawyerDTO response = modelMapper.map(user, ResponseSaveLawyerDTO.class);
        response.setName(user.getName() + " " + user.getLast_name());
        response.setCase_type(lawyer.getCase_type().name());
        response.setBuffet(buffet.getName());

        return response;
    }

    private String uploadAndSaveDocument(MultipartFile file, String code) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String originalName = Optional.ofNullable(file.getOriginalFilename())
                    .orElse("archivo_sin_nombre");
            String newFileName = timestamp + "_" + originalName;
            String s3Key = "buffets/" + code + "/PERFIL/" + newFileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata));
            logger.info("Archivo {} cargado correctamente a S3 en {}", code, s3Key);

            return s3Key;
        } catch (IOException e) {
            logger.error("Error al subir archivo {} a S3: {}", code, e.getMessage(), e);
            throw new RuntimeException("Fallo al subir el archivo " + code + " a S3", e);
        }
    }

    @Override
    @Transactional
    public List<ResponseLawyerSimpleDTO> searchSimpleLawyer(String search, String codeBuffet, CaseType typeCase) {
        return lawyerRepository.searchLawyerByUserCodeOrNameOrLastName(search, codeBuffet, typeCase).stream()
                .map(c -> {
                    String busqueda = c.getUser().getCode() + " - " + c.getUser().getName() + " "
                            + c.getUser().getLast_name();
                    return new ResponseLawyerSimpleDTO(busqueda);
                })
                .toList();
    }

    @Override
    @Transactional
    public ReponseUpdateLawyerDTO updateLawyer(String code, UpdateLawyerDTO dto) {
        logger.info("Actualizando Abogado con código: {}", code);

        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Abogado no encontrado"));

        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setLast_name(dto.getLast_name());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);

        Lawyer lawyer = lawyerRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Datos de abogado no encontrados"));

        lawyer.setCase_type(CaseType.valueOf(dto.getCase_type()));

        lawyerRepository.save(lawyer);

        ReponseUpdateLawyerDTO response = modelMapper.map(user, ReponseUpdateLawyerDTO.class);
        response.setName(lawyer.getUser().getName() + " " + lawyer.getUser().getLast_name());
        response.setBuffet(user.getBuffet().getName());

        return response;
    }

    public SimplePageResponse<ResponseLawyerDTO> findAll(Pageable pageable, String codeBuffet) {
        logger.info("Listando abogados registrados");
        Page<Lawyer> page = lawyerRepository.findByBuffetCode(codeBuffet, pageable);
        Page<ResponseLawyerDTO> dtoPage = page.map(this::responseFullLawyer);
        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseLawyerDTO> listFilterSearch(String search, Pageable pageable, String codeBuffet) {
        logger.info("Listando abogados registrados. Filtro: {}", search);

        Page<Lawyer> page = (search == null || search.trim().isEmpty())
                ? lawyerRepository.findByBuffetCode(codeBuffet, pageable)
                : lawyerRepository.searchLawyerByBuffetCode(search, codeBuffet, pageable);

        Page<ResponseLawyerDTO> dtoPage = page.map(this::responseFullLawyer);
        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseLawyerDTO> findAllByCreationDate(Timestamp startDate, Timestamp endDate,
            Pageable pageable, String codeBuffet) {
        logger.info("Listando abogados registrados entre {} y {}", startDate, endDate);

        Page<Lawyer> page = lawyerRepository.findLawyerByUserCreationDateBetweenAndBuffetCode(startDate, endDate,
                codeBuffet, pageable);
        Page<ResponseLawyerDTO> dtoPage = page.map(this::responseFullLawyer);

        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseLawyerDTO> listFilterSearchAndDate(
            String search,
            Timestamp startDate,
            Timestamp endDate,
            Pageable pageable,
            String codeBuffet) {

        logger.info("Listando Abogados. Filtro texto: '{}', rango fechas: {} a {}", search, startDate, endDate);

        Page<Lawyer> page;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasSearch && hasDateRange) {
            // Filtrar por texto y rango fechas
            page = lawyerRepository.searchLawyerByUserCreationDateBetweenAndBuffetCode(
                    search.trim(), startDate, endDate, codeBuffet, pageable);
        } else if (hasSearch) {
            // Sólo filtro texto
            page = lawyerRepository.searchLawyerByBuffetCode(search, codeBuffet, pageable);
        } else if (hasDateRange) {
            // Sólo filtro rango fechas
            page = lawyerRepository.findLawyerByUserCreationDateBetweenAndBuffetCode(startDate, endDate,
                    codeBuffet, pageable);
        } else {
            // Sin filtros
            page = lawyerRepository.findByBuffetCode(codeBuffet, pageable);
        }

        Page<ResponseLawyerDTO> dtoPage = page.map(this::responseFullLawyer);
        return new SimplePageResponse<>(dtoPage);
    }

    private ResponseLawyerDTO responseFullLawyer(Lawyer lawyer) {
        ResponseLawyerDTO responseLawyerDTO = modelMapper.map(lawyer, ResponseLawyerDTO.class);
        responseLawyerDTO.setFullName(lawyer.getUser().getName() + " " + lawyer.getUser().getLast_name());
        responseLawyerDTO.setEmail(lawyer.getUser().getEmail());
        responseLawyerDTO.setCode(lawyer.getUser().getCode());
        responseLawyerDTO.setPhone(lawyer.getUser().getPhone());
        responseLawyerDTO.setAddress(lawyer.getUser().getAddress());
        responseLawyerDTO.setEnabled(lawyer.getUser().isEnable());
        responseLawyerDTO.setBuffet(lawyer.getUser().getBuffet().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = lawyer.getUser().getCreation().toLocalDateTime().format(formatter);
        responseLawyerDTO.setCreation(formattedDate);
        return responseLawyerDTO;
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
        } while (userRepository.existsByCode(code));
        return code;
    }
}
