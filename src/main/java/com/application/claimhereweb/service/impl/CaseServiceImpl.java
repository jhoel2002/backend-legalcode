package com.application.claimhereweb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
//import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.LegalCase;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.CaseStatus;
//import com.application.claimhereweb.model.entity.enumEntity.CaseStatusRequest;
//import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.LegalCaseRepository;
import com.application.claimhereweb.service.ICaseService;
import com.application.claimhereweb.service.dto.ResponseCaseDTO;
import com.application.claimhereweb.service.dto.SaveCaseDTO;
//import com.application.claimhereweb.service.dto.SaveCaseUserDTO;

@Service
public class CaseServiceImpl implements ICaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    private LegalCaseRepository legalCaseRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    /*
     * @Autowired
     * private CaseRequestRepository caseRequestRepository;
     */

    @Override

    @Transactional
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
        logger.info("Caso guardado con ID: {}", caseModel.getId());
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