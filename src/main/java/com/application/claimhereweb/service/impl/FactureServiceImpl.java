package com.application.claimhereweb.service.impl;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.StatusPayment;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.FactureRepository;
import com.application.claimhereweb.service.IFactureService;
import com.application.claimhereweb.service.dto.ResponseFactureDTO;
import com.application.claimhereweb.service.dto.SaveFactureDTO;
import com.application.claimhereweb.utils.ReportGenerator;

import net.sf.jasperreports.engine.JRException;

@Service
public class FactureServiceImpl implements IFactureService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReportGenerator reportGenerator;

    @Override
    @Transactional
    public ResponseFactureDTO saveFacture(SaveFactureDTO value_case, Long id_customer) {
        logger.info("Registrando la factura del caso: {}", value_case.getLegalCase());

        String customerName = Optional.ofNullable(customerRepository.findCustomerUserNameById(id_customer))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Cliente no encontrado");
                });

        Facture facture = modelMapper.map(value_case, Facture.class);

        facture.setLegal_case(new LegalCase() {
            {
                setId(value_case.getLegalCase());
            }
        });

        facture.setUser(new User() {
            {
                setId(value_case.getUser());
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

    public ResponseFactureDTO responseFacture(Facture factureModel) {
        ResponseFactureDTO response = modelMapper.map(factureModel, ResponseFactureDTO.class);
        response.setCustomer(factureModel.getCustomer().getUser().getName());
        logger.info("Facture guardado con ID: {}", factureModel.getId());
        return response;
    }

    @Override
    public byte[] exportPdf() throws JRException, FileNotFoundException {
        List<ResponseFactureDTO> response = factureRepository.findAll()
            .stream()
            .map(this::responseFacture)
            .collect(Collectors.toList());
        
        return reportGenerator.exportToPdf(response);
    }

    @Override
    public byte[] exportXls() throws JRException, FileNotFoundException {
        List<ResponseFactureDTO> response = factureRepository.findAll()
            .stream()
            .map(this::responseFacture)
            .collect(Collectors.toList());
        return reportGenerator.exportToXls(response);
    }
}
