package com.application.claimhereweb.service.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

//import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICustomerService;
import com.application.claimhereweb.service.dto.ResponseCustomerDTO;
import com.application.claimhereweb.service.dto.ResponseSaveCustomerDTO;
import com.application.claimhereweb.service.dto.SaveCustomerDTO;
import com.application.claimhereweb.model.entity.User;

@Service
public class CustomerServiceImpl implements ICustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Override

    @Transactional

    public ResponseSaveCustomerDTO saveCustomer(SaveCustomerDTO dto) {
        // 1. Mapear SaveCustomerDTO → User
        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user = userRepository.save(user);

        // 2. Insertar en users_roles directamente con ID del user y un ID fijo de rol
        // cliente (2)
        Long role = 2L;
        roleRepository.assignRoleToUser(user.getId(), role); // user.getId() debe devolver un Long no nulo

        // 3. Mapear SaveCustomerDTO → Customer y setear el user
        Customer customer = modelMapper.map(dto, Customer.class);
        customer.setUser(user);
        customer = customerRepository.save(customer);

        // 4. Mapear User a Response DTO
        ResponseSaveCustomerDTO response = modelMapper.map(user, ResponseSaveCustomerDTO.class);
        response.setType_document_customer(customer.getType_document_customer().name());
        response.setDocument(customer.getDocument());

        return response;
    }

    // Listado completo de clientes
    public SimplePageResponse<ResponseCustomerDTO> findAll(Pageable pageable) {
        logger.info("Listando clientes registrados");
        Page<Customer> page = customerRepository.findAll(pageable);
        Page<ResponseCustomerDTO> dtoPage = page.map(this::responseFullCustomer);
        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro de busqueda por texto
    public SimplePageResponse<ResponseCustomerDTO> listFilterSearch(String search, Pageable pageable) {
        logger.info("Listando clientes registrados. Filtro: {}", search);

        Page<Customer> page = (search == null || search.trim().isEmpty())
                ? customerRepository.findAll(pageable)
                : customerRepository.searchCustomers(search.trim(), pageable);

        Page<ResponseCustomerDTO> dtoPage = page.map(this::responseFullCustomer);
        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro de busqueda por rango de fecha
    public SimplePageResponse<ResponseCustomerDTO> findAllByCreationDate(Timestamp startDate, Timestamp endDate,
            Pageable pageable) {
        logger.info("Listando clientes registrados entre {} y {}", startDate, endDate);

        Page<Customer> page = customerRepository.findCustomersByUserCreationDateBetween(startDate, endDate, pageable);
        Page<ResponseCustomerDTO> dtoPage = page.map(this::responseFullCustomer);

        return new SimplePageResponse<>(dtoPage);
    }

    // Filtro por busqueda por rango de fecha y por texto
    public SimplePageResponse<ResponseCustomerDTO> listFilterSearchAndDate(
            String search,
            Timestamp startDate,
            Timestamp endDate,
            Pageable pageable) {

        logger.info("Listando clientes. Filtro texto: '{}', rango fechas: {} a {}", search, startDate, endDate);

        Page<Customer> page;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasSearch && hasDateRange) {
            // Filtrar por texto y rango fechas
            page = customerRepository.searchCustomersByUserCreationDateBetween(
                    search.trim(), startDate, endDate, pageable);
        } else if (hasSearch) {
            // Sólo filtro texto
            page = customerRepository.searchCustomers(search.trim(), pageable);
        } else if (hasDateRange) {
            // Sólo filtro rango fechas
            page = customerRepository.findCustomersByUserCreationDateBetween(startDate, endDate, pageable);
        } else {
            // Sin filtros
            page = customerRepository.findAll(pageable);
        }

        Page<ResponseCustomerDTO> dtoPage = page.map(this::responseFullCustomer);
        return new SimplePageResponse<>(dtoPage);
    }

    private ResponseCustomerDTO responseFullCustomer(Customer customer) {
        ResponseCustomerDTO responseCustomerDTO = modelMapper.map(customer, ResponseCustomerDTO.class);
        responseCustomerDTO.setEmail(customer.getUser().getEmail());
        responseCustomerDTO.setName(customer.getUser().getName());
        responseCustomerDTO.setLast_name(customer.getUser().getLast_name());
        responseCustomerDTO.setPhone(customer.getUser().getPhone());
        responseCustomerDTO.setAddress(customer.getUser().getAddress());
        responseCustomerDTO.setEnabled(customer.getUser().isEnabled());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = customer.getUser().getCreation().toLocalDateTime().format(formatter);
        responseCustomerDTO.setCreation(formattedDate);
        return responseCustomerDTO;
    }

    /*
     * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
     * String formattedDate =
     * caseRequest.getApplication_date().toLocalDateTime().format(formatter);
     * reponseCaseRequestDTO.setApplication_date(formattedDate);
     */
}