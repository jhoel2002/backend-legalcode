package com.application.claimhereweb.service.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;
//import java.util.List;
//import java.util.stream.Collectors;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.Lawyer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
import com.application.claimhereweb.model.entity.enumEntity.CaseType;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.LawyerRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.AssignLawyerDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateCaseRequestDTO;
import com.application.claimhereweb.service.dto.UpdateStatusCaseRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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
        EmailService emailService;

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterFull(
                        String search,
                        Timestamp startDate,
                        Timestamp endDate,
                        String status,
                        Pageable pageable) {

                logger.info("Listando solicitudes de casos. Filtros -> search: {}, startDate: {}, endDate: {}, status: {}",
                                search, startDate, endDate, status);

                Page<CaseRequest> page = caseRequestRepository.findByFilters(
                                search != null && !search.trim().isEmpty() ? search.trim() : null,
                                startDate,
                                endDate,
                                status != null && !status.trim().isEmpty()
                                                ? CaseStatusRequest.valueOf(status.toUpperCase())
                                                : null,
                                pageable);

                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterSearch(String search, Pageable pageable) {
                logger.info("Listando solicitudes de casos registrados. Filtro: {}", search);

                Page<CaseRequest> page = (search == null || search.trim().isEmpty())
                                ? caseRequestRepository.findAll(pageable)
                                : caseRequestRepository.searchCaseRequest(search.trim(), pageable);

                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> findAllbyApplicationDate(Timestamp startDate,
                        Timestamp endDate,
                        Pageable pageable) {
                logger.info("Listando clientes registrados entre {} y {}", startDate, endDate);

                Page<CaseRequest> page = caseRequestRepository.findCaseRequestByApplicationDateBetween(startDate,
                                endDate,
                                pageable);
                Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(String status, Pageable pageable) {
                Page<CaseRequest> caseRequests = caseRequestRepository.findAllByStatusRequest(
                                CaseStatusRequest.valueOf(status.toUpperCase()), pageable);

                Page<ResponseCaseRequestDTO> dtoPage = caseRequests.map(this::responseFullCaseRequest);
                return new SimplePageResponse<>(dtoPage);
        }

        @Override
        @Transactional
        public SimplePageResponse<ResponseCaseRequestDTO> findAll(Pageable pageable) {
                Page<CaseRequest> page = caseRequestRepository.findAll(pageable);
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
        public ResponseCaseRequestDTO save(SaveCaseRequestDTO dto, String codeCustomer) {
                logger.info("Registrando solicitado de registro de caso: {}", dto.getTitle());

                User user = userRepository.findByCode(codeCustomer)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Cliente no encontrado"));

                Customer customer = customerRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO encontrado"));

                Buffet buffet = buffetRepository.findById(user.getBuffet().getId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Buffet no encontrado"));

                CaseRequest caseRequest = modelMapper.map(dto, CaseRequest.class);
                caseRequest.setCode(generateUniqueCode());
                caseRequest.setCustomer(customer);
                caseRequest.setBuffet(buffet);

                CaseRequest saved = caseRequestRepository.save(caseRequest);

                ResponseCaseRequestDTO response = modelMapper.map(saved, ResponseCaseRequestDTO.class);
                response.setCustomer(user.getName() + " " + user.getLast_name());
                response.setBuffet(buffet.getName());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDate = caseRequest.getCreation().toLocalDateTime().format(formatter);
                response.setCreation(formattedDate);

                String logo = buffet.getImg();
                String nombre_customer = user.getName() + " " + user.getLast_name();
                String email = user.getEmail();
                String code_user = user.getCode();
                String solicitud = caseRequest.getCode();

                Map<String, Object> variables = Map.of(
                                "logo_buffet", logo,
                                "nombre_usuario", nombre_customer,
                                "codigo_usuario", code_user,
                                "codigo_solicitud", solicitud);

                String correoDestino = email;
                emailService.sendEmailUsingTemplate("confirmacion_registro_caso", variables, correoDestino);
                logger.info("Correo enviado a: " + correoDestino);

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
}
