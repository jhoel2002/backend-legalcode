package com.application.claimhereweb.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.claimhereweb.exceptions.ResourceNotFoundException;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.Facture;
import com.application.claimhereweb.model.entity.LegalCase;
import com.application.claimhereweb.model.entity.Role;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.StatusPayment;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.FactureRepository;
import com.application.claimhereweb.model.repository.LegalCaseRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.service.IFactureService;
import com.application.claimhereweb.service.dto.ResponseFactureDTO;
import com.application.claimhereweb.service.dto.SaveFactureDTO;

@Service
public class FactureServiceImpl implements IFactureService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LegalCaseRepository legalCaseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public ResponseFactureDTO saveFacture(SaveFactureDTO value_case, Long id_customer) {
        logger.info("Registrando la factura del caso: {}", value_case.getLegalCaseId());

        String title = Optional.ofNullable(legalCaseRepository.findTitleById(value_case.getLegalCaseId()))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Caso Legal no encontrado");
                });

        String rolUser = Optional.ofNullable(roleRepository.findRoleNameByUserId(value_case.getUserId()))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Usuario no encontrado");
                });

        String customerName = Optional.ofNullable(customerRepository.findCustomerUserNameById(id_customer))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cliente no encontrado");
                });

        Facture facture = modelMapper.map(value_case, Facture.class);

        facture.setLegal_case(new LegalCase() {
            {
                setId(value_case.getLegalCaseId());
                setTitle(title);
            }
        });

        facture.setUser(new User() {
            {
                setId(value_case.getUserId());
                setRoles(new HashSet<>(Arrays.asList(new Role() {
                    {
                        setName(rolUser);
                    }
                })));
            }
        });

        facture.setCustomer(new Customer() {
            {
                setId(id_customer);
                setUser(new User() {
                    {
                        setName(customerName);
                    }
                });
            }
        });

        facture.setStatus_payment(StatusPayment.PENDIENTE);

        Facture savedFacture = factureRepository.save(facture);

        return responseFacture(savedFacture);
    }

    public ResponseFactureDTO responseFacture(Facture caseModel) {
        ResponseFactureDTO response = modelMapper.map(caseModel, ResponseFactureDTO.class);
        response.setLegal_case(caseModel.getLegal_case().getTitle());
        response.setUser(caseModel.getUser().getRoles().getClass().getName());
        response.setCustomer(caseModel.getCustomer().getUser().getName());
        logger.info("Facture guardado con ID: {}", caseModel.getId());
        return response;
    }
}
