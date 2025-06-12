package com.application.claimhereweb.service.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;
//import java.util.List;
import java.util.Optional;
//import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
//import com.application.claimhereweb.service.dto.ResponseUserDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;
import com.application.claimhereweb.service.dto.StatusCaseRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseRequestImpl implements ICaseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CaseRequestImpl.class);

    @Autowired
    CaseRequestRepository caseRequestRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    EmailService emailService;

    @Override

    @Transactional

    // Filtro de busqueda por texto, fecha y estado de la solicitud del caso
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
                status != null && !status.trim().isEmpty() ? CaseStatusRequest.valueOf(status.toUpperCase()) : null,
                pageable);

        Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro de busqueda por texto
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterSearch(String search, Pageable pageable) {
        logger.info("Listando solicitudes de casos registrados. Filtro: {}", search);

        Page<CaseRequest> page = (search == null || search.trim().isEmpty())
                ? caseRequestRepository.findAll(pageable)
                : caseRequestRepository.searchCaseRequest(search.trim(), pageable);

        Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro de busqueda por fecha
    public SimplePageResponse<ResponseCaseRequestDTO> findAllbyApplicationDate(Timestamp startDate, Timestamp endDate,
            Pageable pageable) {
        logger.info("Listando clientes registrados entre {} y {}", startDate, endDate);

        Page<CaseRequest> page = caseRequestRepository.findCaseRequestByApplicationDateBetween(startDate, endDate,
                pageable);
        Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro de busqueda por estado de la solicitud
    public SimplePageResponse<ResponseCaseRequestDTO> listFilterStatus(String status, Pageable pageable) {
        Page<CaseRequest> caseRequests = caseRequestRepository.findAllByStatusRequest(
                CaseStatusRequest.valueOf(status.toUpperCase()), pageable);

        Page<ResponseCaseRequestDTO> dtoPage = caseRequests.map(this::responseFullCaseRequest);
        return new SimplePageResponse<>(dtoPage);
    }

    // Listado completo de solicitudes de casos legales
    public SimplePageResponse<ResponseCaseRequestDTO> findAll(Pageable pageable) {
        Page<CaseRequest> page = caseRequestRepository.findAll(pageable);
        Page<ResponseCaseRequestDTO> dtoPage = page.map(this::responseFullCaseRequest);
        return new SimplePageResponse<>(dtoPage);
    }

    private ResponseCaseRequestDTO responseFullCaseRequest(CaseRequest caseRequest) {
        ResponseCaseRequestDTO reponseCaseRequestDTO = modelMapper.map(caseRequest, ResponseCaseRequestDTO.class);
        reponseCaseRequestDTO.setCustomer(caseRequest.getCustomer().getUser().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = caseRequest.getCreation().toLocalDateTime().format(formatter);
        reponseCaseRequestDTO.setApplication_date(formattedDate);
        return reponseCaseRequestDTO;
    }

    public ResponseCaseDTO statusCaseRequest(StatusCaseRequestDTO dto) {
        logger.info("Actualizando estado de la solicitud de caso legal");

        CaseRequest caseRequest = caseRequestRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de Caso Legal no encontrada :c"));

        String newStatus = dto.getStatus_request();
        CaseStatusRequest caseStatusRequest;

        try {
            caseStatusRequest = CaseStatusRequest.valueOf(newStatus.toUpperCase());

            caseRequest.setStatus_request(caseStatusRequest);
            caseRequestRepository.save(caseRequest);

            // Armando cuerpo del mensaje

            String nombre_customer = caseRequest.getCustomer().getUser().getName();
            String apellido_customer = caseRequest.getCustomer().getUser().getLast_name();

            String customer = nombre_customer + " " + apellido_customer;

            String abogado = "Coordinación General";

            String estadoSolicitud = caseStatusRequest.toString();

            Map<String, Object> variables = Map.of(
                    "estado_solicitud", estadoSolicitud,
                    "nombre_cliente", customer,
                    "nombre_abogado", abogado);

            String correoDestino = caseRequest.getCustomer().getUser().getEmail();

            emailService.sendEmailUsingTemplate("status_caso_legal", variables, correoDestino);
            logger.info("Correo enviado a: " + correoDestino);

            logger.info("Validando estado de la solicitud :D");

            if (caseStatusRequest == CaseStatusRequest.APPROVED) {
                logger.info("La solicitud ha sido APROBADA :D");

            } else {
                logger.info("La solicitud ha sido RECHAZADA :c");
                throw new ResourceNotFoundException("La solicitud no puede continuar porque fue rechazada");
            }

        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("El estado ingresado en 'status_request' es inválido: " + newStatus);
        }

        return null;
    }

    public ResponseCaseRequestDTO saveCaseRequest(SaveCaseRequestDTO saveCaseDTO, Long id_customer) {

        logger.info("Registrando solicitado de registro de caso: {}", saveCaseDTO.getTitle());
        String customerName = Optional.ofNullable(customerRepository.findCustomerUserNameById(id_customer))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cliente no encontrado");
                });

        CaseRequest caseRequest = modelMapper.map(saveCaseDTO, CaseRequest.class);
        caseRequest.setCustomer(new Customer() {
            {
                setId(id_customer);
                setUser(new User() {
                    {
                        setName(customerName);
                    }
                });
            }
        });

        CaseRequest saveCaseRequest = caseRequestRepository.save(caseRequest);
        return responseCaseRequest(saveCaseRequest);
    }

    public ResponseCaseRequestDTO responseCaseRequest(CaseRequest caseModel) {
        ResponseCaseRequestDTO response = modelMapper.map(caseModel, ResponseCaseRequestDTO.class);
        // response.setArea(caseModel.getArea().getName());
        response.setCustomer(caseModel.getCustomer().getUser().getName());
        logger.info("Solicitud de caso guardado con ID: {}", caseModel.getId());
        return response;
    }
}
