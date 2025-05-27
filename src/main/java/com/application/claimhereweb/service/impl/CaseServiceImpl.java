package com.application.claimhereweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
//import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.LegalCase;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatus;
//import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
//import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.LegalCaseRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICaseService;
import com.application.claimhereweb.service.dto.ReponseUpdateLawyer;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.ResponseStatusUpdateCase;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
//import com.application.claimhereweb.service.dto.SaveCaseUserDTO;
import com.application.claimhereweb.service.dto.UpdateLawyer;
import com.application.claimhereweb.service.dto.UpdateStatusCase;

@Service
public class CaseServiceImpl implements ICaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    private LegalCaseRepository legalCaseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /*
     * @Autowired
     * private CaseRequestRepository caseRequestRepository;
     */

    @Override

    @Transactional

    public ResponseStatusUpdateCase statusCase(UpdateStatusCase updateStatusCase) {
        logger.info("Cambiando el estado del caso legal");
        LegalCase legalCase = legalCaseRepository.findById(updateStatusCase.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró el caso legal con el ID proporcionado."));
        CaseStatus caseStatus = CaseStatus.valueOf(updateStatusCase.getStatus_case().toUpperCase());
        legalCase.setStatus_case(caseStatus);

        legalCaseRepository.save(legalCase);

        ResponseStatusUpdateCase responseStatusUpdateCase = new ResponseStatusUpdateCase();
        responseStatusUpdateCase.setId(legalCase.getId());
        responseStatusUpdateCase.setTitle(legalCase.getTitle());
        responseStatusUpdateCase.setType_case(legalCase.getType_case().name());
        responseStatusUpdateCase.setStatus_case(legalCase.getStatus_case().name());
        return responseStatusUpdateCase;
    }

    public ReponseUpdateLawyer assignLawyer(UpdateLawyer updateLawyer) {
        logger.info("Asignando Abogado al caso legal indicado");

        Long validateRoleLawyer = Optional.ofNullable(
                legalCaseRepository.findLawyerIdByUserId(updateLawyer.getUser()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no tiene asignado el rol de abogado :c"));

        LegalCase legalCase = legalCaseRepository.findById(updateLawyer.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se encontró el caso legal con el ID proporcionado."));

        User lawyer = userRepository.findById(validateRoleLawyer)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con el ID proporcionado."));

        legalCase.setUser(lawyer);

        legalCaseRepository.save(legalCase);

        ReponseUpdateLawyer response = new ReponseUpdateLawyer();
        response.setId(legalCase.getId());
        response.setCaseType(legalCase.getType_case().name());
        response.setName(lawyer.getName());
        response.setRole(lawyer.getFirstRoleName());

        return response;
    }

    public SimplePageResponse<ResponseCaseDTO> listFilterSearchAndDate(
            String search,
            Timestamp startDate,
            Timestamp endDate,
            Pageable pageable) {
        logger.info("Listando casos. Filtro texto: '{}', rango fechas: {} a {}", search, startDate, endDate);

        Page<LegalCase> page;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasSearch && hasDateRange) {
            // Filtrar por texto y rango fechas
            page = legalCaseRepository.listFilterFull(
                    search.trim(), startDate, endDate, pageable);
        } else if (hasSearch) {
            // Sólo filtro texto
            page = legalCaseRepository.searchLegalCase(search.trim(), pageable);
        } else if (hasDateRange) {
            // Sólo filtro rango fechas
            page = legalCaseRepository.findCaseByStartDateBetween(startDate, endDate, pageable);
        } else {
            // Sin filtros
            page = legalCaseRepository.findAll(pageable);
        }

        Page<ResponseCaseDTO> dtoPage = page.map(this::responseCase);
        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseCaseDTO> listFilterSearch(String search, Pageable pageable) {
        logger.info("Listando casos registrados. Filtro: {}", search);
        Page<LegalCase> page = (search == null || search.trim().isEmpty())
                ? legalCaseRepository.findAll(pageable)
                : legalCaseRepository.searchLegalCase(search.trim(), pageable);
        Page<ResponseCaseDTO> dtoPage = page.map(this::responseCase);
        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseCaseDTO> findAllByStartDate(Timestamp startDate, Timestamp endDate,
            Pageable pageable) {
        logger.info("Listando casos registrados entre {} y {}", startDate, endDate);

        Page<LegalCase> page = legalCaseRepository.findCaseByStartDateBetween(startDate, endDate, pageable);
        Page<ResponseCaseDTO> dtoPage = page.map(this::responseCase);
        return new SimplePageResponse<>(dtoPage);
    }

    public SimplePageResponse<ResponseCaseDTO> findAll(Pageable pageable) {
        logger.info("Listando casos legales registrados");
        Page<LegalCase> page = legalCaseRepository.findAll(pageable);
        Page<ResponseCaseDTO> dtoPage = page.map(this::responseCase);
        return new SimplePageResponse<>(dtoPage);
    }

    public ResponseCaseDTO saveCaseAdministrator(SaveCaseDTO dto, Long id_customer) {
        logger.info("Caso Registrado por Administrador :D");
        logger.info("Registrando caso con título: {}", dto.getTitle());

        /*
         * String areaName = Optional.ofNullable(areaRepository.findNameById(id_area))
         * .orElseThrow(() -> {
         * return new ResourceNotFoundException("Área no encontrada");
         * });
         */

        String customerName = Optional.ofNullable(customerRepository.findCustomerUserNameById(id_customer))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cliente no encontrado");
                });
        LegalCase legalCase = modelMapper.map(dto, LegalCase.class);
        legalCase.setCustomer(new Customer() {
            {
                setId(id_customer);
                setUser(new User() {
                    {
                        setName(customerName);
                    }
                });
            }
        });
        legalCase.setStatus_case(CaseStatus.NEW);

        LegalCase savedCase = legalCaseRepository.save(legalCase);
        return responseCase(savedCase);
    }

    public ResponseCaseDTO responseCase(LegalCase caseModel) {
        ResponseCaseDTO response = modelMapper.map(caseModel, ResponseCaseDTO.class);
        response.setCustomer(caseModel.getCustomer().getUser().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = caseModel.getStart_date().toLocalDateTime().format(formatter);
        response.setStart_date(formattedDate);
        return response;
    }

    /*
     * public void validate_case_request(CaseStatusRequest status_request) {
     * logger.info("Validando estado de la solicitud :D");
     * 
     * if (!CaseStatusRequest.APPROVED.equals(status_request)) {
     * String mensajeError =
     * "La solicitud de caso no está aprobada. Estado actual: " + status_request;
     * throw new ResourceNotFoundException(mensajeError);
     * }
     * }
     */

    /*
     * public ResponseCaseDTO saveCaseUser(SaveCaseUserDTO dto) {
     * logger.info("Caso Registrado por Usuario :D");
     * LegalCase legalCase = modelMapper.map(dto, LegalCase.class);
     * 
     * Long validate_role_lawyer =
     * Optional.ofNullable(legalCaseRepository.findLawyerIdByUserId(dto.getUser()))
     * .orElseThrow(() -> {
     * return new
     * ResourceNotFoundException("Usuario no tiene asignado el role de abogado :c");
     * });
     * 
     * legalCase.setUser(new User() {
     * {
     * setId(validate_role_lawyer);
     * }
     * });
     * 
     * CaseRequest caseRequest =
     * caseRequestRepository.findCaseRequestById(dto.getCaseRequest())
     * .orElseThrow(() -> new
     * ResourceNotFoundException("Solicitud de caso no encontrada :c"));
     * 
     * validate_case_request(caseRequest.getStatus_request());
     * logger.info(caseRequest.getStatus_request().toString());
     * 
     * legalCase.setCase_request(new CaseRequest() {
     * {
     * setId(caseRequest.getId());
     * }
     * });
     * 
     * legalCase.setTitle(caseRequest.getTitle());
     * legalCase.setDescription(caseRequest.getDescription());
     * legalCase.setType_case(caseRequest.getType_case());
     * legalCase.setCustomer(caseRequest.getCustomer());
     * 
     * legalCase.setStatus_case(CaseStatus.NEW);
     * LegalCase savedCase = legalCaseRepository.save(legalCase);
     * return responseCase(savedCase);
     * }
     */
}