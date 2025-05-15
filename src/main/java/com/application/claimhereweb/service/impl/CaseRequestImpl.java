package com.application.claimhereweb.service.impl;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
import com.application.claimhereweb.model.entity.CaseRequest;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.repository.CaseRequestRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.service.ICaseRequestService;
import com.application.claimhereweb.service.dto.ResponseCaseRequestDTO;
import com.application.claimhereweb.service.dto.SaveCaseRequestDTO;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseRequestImpl implements ICaseRequestService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    CaseRequestRepository caseRequestRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override

    @Transactional
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
        // status_request por default es PENDING esta definido en la entidad
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
