package com.application.claimhereweb.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
//import java.util.List;
//import java.util.stream.Collectors;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.model.S3Object;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.Document;
import com.application.claimhereweb.model.entity.Lawyer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;
import com.application.claimhereweb.model.entity.enumEntity.DocumentType;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.DocumentRepository;
import com.application.claimhereweb.model.repository.LawyerRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.AssignLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestInfoDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateStatusCaseRequestDTO;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CaseRequestImpl implements ICaseRequestService {

        private static final Logger logger = LoggerFactory.getLogger(CaseRequestImpl.class);

        @Autowired
        CaseRequestRepository caseRequestRepository;

        @Autowired
        CustomerRepository customerRepository;

        @Autowired
        BuffetRepository buffetRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        LawyerRepository lawyerRepository;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        DocumentRepository documentRepository;

        @Autowired
        EmailService emailService;

        @Value("${aws.s3.bucket}")
        private String bucketName;

        @Autowired
        private AmazonS3 amazonS3;

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterFull(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        String status,
                        Pageable pageable,
                        String codeBuffet) {

                logger.info("Listando solicitudes de casos. Filtros -> search: {}, startDate: {}, endDate: {}, status: {}",
                                search, startDate, endDate, status);

                if (status.equals("PENDING")) {
                        Page<CaseRequest> pagePending = caseRequestRepository.findByFiltersPending(
                                        search != null && !search.trim().isEmpty() ? search.trim() : null,
                                        startDate,
                                        endDate,
                                        codeBuffet,
                                        pageable);
                        Page<ResponseCaseRequestDTO> dtoPagePending = pagePending.map(this::responseFullCaseRequest);
                        return new SimplePageResponse<>(dtoPagePending);
                }

                Page<CaseRequest> page = caseRequestRepository.findByFilters(
                                search != null && !search.trim().isEmpty() ? search.trim() : null,
                                startDate,
                                endDate,
                                status != null && !status.trim().isEmpty()
                                                ? CaseStatusRequest.valueOf(status.toUpperCase())
                                                : null,
                                codeBuffet,
                                pageable);

                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterSearch(String search, Pageable pageable,
                        String codeBuffet, String status) {
                logger.info("Listando solicitudes de casos registrados. Filtro: {}", search);
                if (status.equals("PENDING")) {
                        Page<CaseRequest> pagePending = (search == null || search.trim().isEmpty())
                                        ? caseRequestRepository.findAllFilteredStatusByBuffetCode(codeBuffet, pageable)
                                        : caseRequestRepository.searchCaseRequestByBuffetCodePending(search.trim(),
                                                        codeBuffet,
                                                        pageable);

                        Page<ResponseCaseRequestDTO> dtoPagePending = pagePending.map(this::responseFullCaseRequest);
                        return new SimplePageResponse<>(dtoPagePending);
                }

                Page<CaseRequest> page = (search == null || search.trim().isEmpty())
                                ? caseRequestRepository.findAllByStatusRequestAndBuffetCode(
                                                CaseStatusRequest.valueOf(status.toUpperCase()), codeBuffet, pageable)
                                : caseRequestRepository.searchCaseRequestByBuffetCode(search.trim(),
                                                codeBuffet,
                                                pageable,
                                                status);

                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> findAllbyApplicationDate(Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable,
                        String codeBuffet,
                        String status) {
                logger.info("Listando clientes registrados entre {} y {}", startDate, endDate);

                if (status.equals("PENDING")) {
                        Page<CaseRequest> pagePending = caseRequestRepository
                                        .findCaseRequestByBuffetCodeAndCreationBetweenPending(codeBuffet,
                                                        startDate,
                                                        endDate,
                                                        pageable);
                        Page<ResponseCaseRequestDTO> dtoPagePending = pagePending.map(this::responseFullCaseRequest);
                        return new SimplePageResponse<>(dtoPagePending);
                }

                Page<CaseRequest> page = caseRequestRepository.findCaseRequestByBuffetCodeAndCreationBetween(codeBuffet,
                                startDate,
                                endDate,
                                status,
                                pageable);
                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(String status, Pageable pageable,
                        String codeBuffet) {

                if (status.equals("PENDING")) {
                        Page<CaseRequest> pendingRequest = caseRequestRepository
                                        .findAllFilteredStatusByBuffetCode(codeBuffet, pageable);
                        Page<ResponseCaseRequestDTO> dtoPendingPage = pendingRequest.map(this::responseFullCaseRequest);
                        return new SimplePageResponse<>(dtoPendingPage);
                }

                Page<CaseRequest> caseRequests = caseRequestRepository.findAllByStatusRequestAndBuffetCode(
                                CaseStatusRequest.valueOf(status.toUpperCase()), codeBuffet, pageable);

                Page<ResponseCaseRequestDTO> dtoPage = caseRequests.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> findAll(Pageable pageable, String code) {
                Page<CaseRequest> page = caseRequestRepository.findAllByBuffetCode(code, pageable);
                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public void assignLawyerCase(AssignLawyerDTO dto) {
                CaseRequest caseRequest = caseRequestRepository.findByCode(dto.getCode())
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Solicitud de Caso no encontrado"));

                User infoLawyer = userRepository.findByCode(dto.getLawyer())
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Codigo no encontrado"));

                Lawyer lawyer = lawyerRepository.findByUserId(infoLawyer.getId())
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Abogado no encontrado"));

                if (lawyer.getCase_type() != caseRequest.getType_case()) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "El abogado no tiene el mismo tipo de caso legal");
                }

                Customer customer = caseRequest.getCustomer();
                User infoCustomer = customer.getUser();
                Buffet buffet = caseRequest.getBuffet();

                caseRequest.setLawyer(lawyer);
                caseRequestRepository.save(caseRequest);

                String logo = buffet.getImg();
                String nombre_customer = infoCustomer.getName() + " " + infoCustomer.getLast_name();
                String code_case = caseRequest.getCode();
                String email = infoCustomer.getEmail();
                String code_lawyer = infoLawyer.getCode();
                String name_lawyer = infoLawyer.getName() + " " + infoLawyer.getLast_name();

                Map<String, Object> variables = Map.of(
                                "logo_buffet", logo,
                                "nombre_usuario", nombre_customer,
                                "codigo_solicitud", code_case,
                                "codigo_abogado", code_lawyer,
                                "nombre_abogado", name_lawyer);

                String correoDestino = email;
                emailService.sendEmailUsingTemplate("asignacion_abogado", variables, correoDestino);
                logger.info("Correo enviado a: " + correoDestino);
        }

        @Override
        @Transactional
        public void updateStatusCaseRequest(UpdateStatusCaseRequestDTO dto) {
                CaseRequest caseRequest = caseRequestRepository.findByCode(dto.getCode())
                                .orElseThrow(
                                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                                "Solicitud de Caso no encontrado"));

                caseRequest.setStatus_request(CaseStatusRequest.valueOf(dto.getStatus_request()));
                caseRequestRepository.save(caseRequest);

                Customer customer = caseRequest.getCustomer();
                User user = customer.getUser();
                Buffet buffet = caseRequest.getBuffet();

                String logo = buffet.getImg();
                String nombre_customer = user.getName() + " " + user.getLast_name();
                String code_case = caseRequest.getCode();
                String email = user.getEmail();
                String status = caseRequest.getStatus_request().name();

                Map<String, Object> variables = Map.of(
                                "logo_buffet", logo,
                                "nombre_usuario", nombre_customer,
                                "codigo_solicitud", code_case,
                                "status_solicitud", status);

                String correoDestino = email;
                emailService.sendEmailUsingTemplate("actualizacion_estado_caso", variables, correoDestino);
                logger.info("Correo enviado a: " + correoDestino);
        }

        @Override
        @Transactional
        public ResponseCaseRequestInfoDTO searchInfo(String codeCaseRequest) {
                logger.info("Obteniendo información detallada de la solicitud de caso con código: {}", codeCaseRequest);

                CaseRequest caseRequest = caseRequestRepository.findByCode(codeCaseRequest)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Solicitud de caso no encontrada"));

                Document quotation = documentRepository.findQuotationByCaseRequestCode(codeCaseRequest).orElse(null);

                List<Document> evidencias = documentRepository.findEvidenceByCaseRequestCode(codeCaseRequest);

                List<Map<String, String>> evidenciaList = evidencias.stream()
                                .map(doc -> Map.of("code", doc.getCode(), "name", doc.getName()))
                                .toList();

                ResponseCaseRequestInfoDTO dto = new ResponseCaseRequestInfoDTO();
                dto.setTitle(caseRequest.getTitle());
                dto.setCode(caseRequest.getCode());
                dto.setType_case(caseRequest.getType_case().toString());
                dto.setStatus_request(caseRequest.getStatus_request().toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = caseRequest.getCreation().toLocalDateTime().format(formatter);
                dto.setCreation(formattedDate);

                if (caseRequest.getLawyer() != null) {
                        dto.setLawyerName(caseRequest.getLawyer().getUser().getName() + " "
                                        + caseRequest.getLawyer().getUser().getLast_name());
                }

                dto.setDescription(caseRequest.getDescription());
                dto.setCustomerName(caseRequest.getCustomer().getUser().getName() + " "
                                + caseRequest.getCustomer().getUser().getLast_name());
                dto.setCustomerEmail(caseRequest.getCustomer().getUser().getEmail());
                dto.setCustomerDocumentType(caseRequest.getCustomer().getDocument_type().toString());
                dto.setCustomerDocumentNumber(caseRequest.getCustomer().getDocument_number());

                dto.setEvidencias(evidenciaList);
                if (quotation != null) {
                        dto.setCotizacion(Map.of(
                                        "code", quotation.getCode(),
                                        "name", quotation.getName()));
                }
                return dto;
        }

        @Override
        @Transactional
        public ResponseCaseRequestDTO updateInfo(UpdateCaseRequestDTO dto, String code) {
                logger.info("Actualizando solicitud de caso legal con código: {}", code);
                CaseRequest caseRequest = caseRequestRepository.findByCode(code)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Solicitud de caso no encontrada"));

                if (caseRequest.getLawyer() != null) {
                        Lawyer lawyer = caseRequest.getLawyer();
                        CaseType nuevoTipo = CaseType.valueOf(dto.getType_case());

                        if (nuevoTipo != lawyer.getCase_type()) {
                                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "No se puede actualizar el tipo de caso, porque ya tiene un abogado asignado");
                        } else {
                                logger.info("El tipo de caso es el mismo que el del abogado asignado. No se actualiza el campo.");
                        }
                } else {
                        caseRequest.setType_case(CaseType.valueOf(dto.getType_case()));
                }

                Customer customer = caseRequest.getCustomer();
                User user = customer.getUser();
                Buffet buffet = user.getBuffet();

                caseRequest.setTitle(dto.getTitle());
                caseRequest.setDescription(dto.getDescription());

                CaseRequest saved = caseRequestRepository.save(caseRequest);

                ResponseCaseRequestDTO response = modelMapper.map(saved, ResponseCaseRequestDTO.class);

                response.setCustomer(user.getName() + " " + user.getLast_name());
                response.setBuffet(buffet.getName());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = caseRequest.getCreation().toLocalDateTime().format(formatter);
                response.setCreation(formattedDate);

                return response;
        }

        @Override
        @Transactional
        public ResponseCaseRequestDTO saveEvidenceMassive(SaveCaseRequestDTO dto, String codeCustomer,
                        MultipartFile[] evidencia) {
                logger.info("Registrando solicitud de caso: {}", dto.getTitle());

                User user = userRepository.findByCode(codeCustomer)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no encontrado"));

                Customer customer = customerRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no asociado"));

                Buffet buffet = buffetRepository.findById(user.getBuffet().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Buffet no encontrado"));

                CaseRequest caseRequest = modelMapper.map(dto, CaseRequest.class);
                caseRequest.setCode(generateUniqueCode());
                caseRequest.setCustomer(customer);
                caseRequest.setBuffet(buffet);

                CaseRequest savedCase = caseRequestRepository.save(caseRequest);

                ResponseCaseRequestDTO response = modelMapper.map(savedCase, ResponseCaseRequestDTO.class);
                response.setCustomer(user.getName() + " " + user.getLast_name());
                response.setBuffet(buffet.getName());
                response.setCreation(savedCase.getCreation().toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                if (evidencia != null && evidencia.length > 0) {
                        List<String> allowedMimeTypes = List.of(
                                        "image/png", "image/jpeg", "image/jpg",
                                        "application/pdf", "text/plain", "text/csv",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                        for (MultipartFile file : evidencia) {
                                if (file == null || file.isEmpty())
                                        continue;

                                String mimeType = file.getContentType();
                                if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                                        throw new IllegalArgumentException("Tipo de archivo no permitido: " + mimeType);
                                }

                                try {
                                        String timestamp = String.valueOf(System.currentTimeMillis());
                                        String originalName = Optional.ofNullable(file.getOriginalFilename())
                                                        .orElse("archivo_sin_nombre");
                                        String newFileName = timestamp + "_" + originalName;
                                        String s3Key = "buffets/" + buffet.getCode() + "/EVIDENCE/"
                                                        + caseRequest.getCode() + "/" + newFileName;

                                        ObjectMetadata metadata = new ObjectMetadata();
                                        metadata.setContentLength(file.getSize());
                                        metadata.setContentType(mimeType);

                                        amazonS3.putObject(new PutObjectRequest(bucketName, s3Key,
                                                        file.getInputStream(), metadata));
                                        logger.info("Archivo cargado correctamente a S3 con clave {}", s3Key);

                                        Document document = new Document();
                                        document.setCode(generateUniqueCodeDocument());
                                        document.setType_document(DocumentType.EVIDENCE);
                                        document.setName(originalName);
                                        document.setUrl(s3Key);
                                        document.setBuffet(buffet);
                                        document.setCase_request(savedCase);

                                        documentRepository.save(document);

                                } catch (IOException e) {
                                        logger.error("Error al subir archivo a S3: {}", e.getMessage(), e);
                                        throw new RuntimeException("Fallo al subir la evidencia a S3", e);
                                }
                        }
                }

                try {
                        Map<String, Object> variables = Map.of(
                                        "logo_buffet", buffet.getImg(),
                                        "nombre_usuario", user.getName() + " " + user.getLast_name(),
                                        "codigo_usuario", user.getCode(),
                                        "codigo_solicitud", savedCase.getCode());
                        emailService.sendEmailUsingTemplate("confirmacion_registro_caso", variables, user.getEmail());
                        logger.info("Correo enviado a: {}", user.getEmail());
                } catch (Exception e) {
                        logger.warn("No se pudo enviar el correo a {}: {}", user.getEmail(), e.getMessage());
                }

                return response;
        }

        @Override
        @Transactional
        public ResponseEntity<byte[]> downloadDocument(String codeDocument) {
                Document document = documentRepository.findByCode(codeDocument)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Documento no encontrado"));

                String s3Key = document.getUrl();

                try (S3Object s3Object = amazonS3.getObject(bucketName, s3Key);
                                S3ObjectInputStream inputStream = s3Object.getObjectContent()) {

                        byte[] content = inputStream.readAllBytes();
                        String fileName = document.getName();

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

                        return new ResponseEntity<>(content, headers, HttpStatus.OK);

                } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                        "No se pudo descargar el archivo desde S3", e);
                }
        }

        @Override
        @Transactional
        public ResponseCaseRequestDTO saveEvidenceMassiveQuotation(SaveCaseRequestDTO dto, String codeCustomer,
                        MultipartFile[] evidencia, MultipartFile[] cotizacion) {

                logger.info("Validando archivos antes de procesar solicitud: {}", dto.getTitle());

                if (cotizacion != null && cotizacion.length > 1) {
                        throw new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Solo se permite un archivo de cotización.");
                }

                List<String> allowedMimeTypes = List.of(
                                "image/png", "image/jpeg", "image/jpg",
                                "application/pdf", "text/plain", "text/csv",
                                "application/msword",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "application/vnd.ms-excel",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                if (cotizacion != null && cotizacion.length == 1) {
                        MultipartFile archivo = cotizacion[0];
                        String mimeType = archivo.getContentType();
                        if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                                throw new ResponseStatusException(
                                                HttpStatus.INTERNAL_SERVER_ERROR,
                                                "Tipo de archivo no permitido: " + mimeType);
                        }
                }

                if (evidencia != null && evidencia.length > 0) {
                        for (MultipartFile file : evidencia) {
                                if (file == null || file.isEmpty())
                                        continue;

                                String mimeType = file.getContentType();
                                if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                                        throw new ResponseStatusException(
                                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Tipo de archivo no permitido: " + mimeType);
                                }
                        }
                }

                logger.info("Validaciones completadas, registrando solicitud de caso: {}", dto.getTitle());

                User user = userRepository.findByCode(codeCustomer)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no encontrado"));

                Customer customer = customerRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no asociado"));

                Buffet buffet = buffetRepository.findById(user.getBuffet().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Buffet no encontrado"));

                CaseRequest caseRequest = modelMapper.map(dto, CaseRequest.class);
                caseRequest.setCode(generateUniqueCode());
                caseRequest.setCustomer(customer);
                caseRequest.setBuffet(buffet);

                CaseRequest savedCase = caseRequestRepository.save(caseRequest);

                ResponseCaseRequestDTO response = modelMapper.map(savedCase, ResponseCaseRequestDTO.class);
                response.setCustomer(user.getName() + " " + user.getLast_name());
                response.setBuffet(buffet.getName());
                response.setCreation(savedCase.getCreation().toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                if (evidencia != null && evidencia.length > 0) {
                        for (MultipartFile file : evidencia) {
                                if (file == null || file.isEmpty())
                                        continue;
                                uploadAndSaveDocument(file, buffet, savedCase, DocumentType.EVIDENCE);
                        }
                }

                if (cotizacion != null && cotizacion.length == 1) {
                        MultipartFile archivo = cotizacion[0];
                        if (!archivo.isEmpty()) {
                                uploadAndSaveDocument(archivo, buffet, savedCase, DocumentType.QUOTATION);
                        }
                }

                try {
                        Map<String, Object> variables = Map.of(
                                        "logo_buffet", buffet.getImg(),
                                        "nombre_usuario", user.getName() + " " + user.getLast_name(),
                                        "codigo_usuario", user.getCode(),
                                        "codigo_solicitud", savedCase.getCode());
                        emailService.sendEmailUsingTemplate("confirmacion_registro_caso", variables, user.getEmail());
                        logger.info("Correo enviado a: {}", user.getEmail());
                } catch (Exception e) {
                        logger.warn("No se pudo enviar el correo a {}: {}", user.getEmail(), e.getMessage());
                }

                return response;
        }

        private void uploadAndSaveDocument(MultipartFile file, Buffet buffet, CaseRequest caseRequest,
                        DocumentType tipo) {
                try {
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        String originalName = Optional.ofNullable(file.getOriginalFilename())
                                        .orElse("archivo_sin_nombre");
                        String newFileName = timestamp + "_" + originalName;

                        String carpeta = tipo == DocumentType.QUOTATION ? "QUOTATION" : "EVIDENCE";
                        String s3Key = "buffets/" + buffet.getCode() + "/" + carpeta + "/" + caseRequest.getCode() + "/"
                                        + newFileName;

                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(file.getSize());
                        metadata.setContentType(file.getContentType());

                        amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(), metadata));
                        logger.info("Archivo {} cargado correctamente a S3 en {}", tipo, s3Key);

                        Document document = new Document();
                        document.setCode(generateUniqueCodeDocument());
                        document.setType_document(tipo);
                        document.setName(originalName);
                        document.setUrl(s3Key);
                        document.setBuffet(buffet);
                        document.setCase_request(caseRequest);

                        documentRepository.save(document);

                } catch (IOException e) {
                        logger.error("Error al subir archivo {} a S3: {}", tipo, e.getMessage(), e);
                        throw new RuntimeException("Fallo al subir el archivo " + tipo + " a S3", e);
                }
        }

        @Override
        @Transactional
        public ResponseCaseRequestDTO save(SaveCaseRequestDTO dto, String codeCustomer, MultipartFile file) {
                logger.info("Registrando solicitud de caso: {}", dto.getTitle());

                User user = userRepository.findByCode(codeCustomer)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no encontrado"));

                Customer customer = customerRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no asociado"));

                Buffet buffet = buffetRepository.findById(user.getBuffet().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Buffet no encontrado"));

                CaseRequest caseRequest = modelMapper.map(dto, CaseRequest.class);
                caseRequest.setCode(generateUniqueCode());
                caseRequest.setCustomer(customer);
                caseRequest.setBuffet(buffet);

                CaseRequest savedCase = caseRequestRepository.save(caseRequest);

                ResponseCaseRequestDTO response = modelMapper.map(savedCase, ResponseCaseRequestDTO.class);
                response.setCustomer(user.getName() + " " + user.getLast_name());
                response.setBuffet(buffet.getName());
                response.setCreation(savedCase.getCreation().toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                if (file != null && !file.isEmpty()) {
                        String mimeType = file.getContentType();
                        List<String> allowedMimeTypes = List.of(
                                        "image/png", "image/jpeg", "image/jpg",
                                        "application/pdf", "text/plain", "text/csv",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

                        if (mimeType == null || !allowedMimeTypes.contains(mimeType)) {
                                throw new IllegalArgumentException("Tipo de archivo no permitido: " + mimeType);
                        }

                        try {
                                String timestamp = String.valueOf(System.currentTimeMillis());
                                String originalName = Optional.ofNullable(file.getOriginalFilename())
                                                .orElse("archivo_sin_nombre");
                                String newFileName = timestamp + "_" + originalName;
                                String s3Key = "buffets/" + buffet.getCode() + "/EVIDENCE/" + newFileName;

                                ObjectMetadata metadata = new ObjectMetadata();
                                metadata.setContentLength(file.getSize());
                                metadata.setContentType(mimeType);

                                amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, file.getInputStream(),
                                                metadata));
                                logger.info("Archivo cargado correctamente a S3 con clave {}", s3Key);

                                Document document = new Document();
                                document.setCode(generateUniqueCodeDocument());
                                document.setType_document(DocumentType.EVIDENCE);
                                document.setName(originalName);
                                document.setUrl(s3Key);
                                document.setBuffet(buffet);
                                document.setCase_request(savedCase);

                                documentRepository.save(document);

                        } catch (IOException e) {
                                logger.error("Error al subir archivo a S3: {}", e.getMessage(), e);
                                throw new RuntimeException("Fallo al subir la evidencia a S3", e);
                        }
                }

                try {
                        Map<String, Object> variables = Map.of(
                                        "logo_buffet", buffet.getImg(),
                                        "nombre_usuario", user.getName() + " " + user.getLast_name(),
                                        "codigo_usuario", user.getCode(),
                                        "codigo_solicitud", savedCase.getCode());
                        emailService.sendEmailUsingTemplate("confirmacion_registro_caso", variables, user.getEmail());
                        logger.info("Correo enviado a: {}", user.getEmail());
                } catch (Exception e) {
                        logger.warn("No se pudo enviar el correo a {}: {}", user.getEmail(), e.getMessage());
                }

                return response;
        }

        private ResponseCaseRequestDTO responseFullCaseRequest(CaseRequest caseRequest) {
                ResponseCaseRequestDTO reponseCaseRequestDTO = modelMapper.map(caseRequest,
                                ResponseCaseRequestDTO.class);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = caseRequest.getCreation().toLocalDateTime().format(formatter);
                reponseCaseRequestDTO.setCreation(formattedDate);
                reponseCaseRequestDTO.setCustomer(caseRequest.getCustomer().getUser().getName() + " "
                                + caseRequest.getCustomer().getUser().getLast_name());
                reponseCaseRequestDTO.setBuffet(caseRequest.getBuffet().getName());
                return reponseCaseRequestDTO;
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

        private String generateUniqueCodeDocument() {
                String code;
                do {
                        code = generateCode();
                } while (documentRepository.existsByCode(code));
                return code;
        }
}
