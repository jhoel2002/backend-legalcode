package com.application.claimhereweb.service.impl;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

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
import org.springframework.web.server.ResponseStatusException;

import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.ICustomerService;
import com.application.claimhereweb.service.dto.ResponseCustomerDTO;
import com.application.claimhereweb.service.dto.ResponseSaveCustomerDTO;
import com.application.claimhereweb.service.dto.SaveCustomerDTO;
import com.application.claimhereweb.service.dto.UpdateCustomerDTO;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.DocumentCustomertype;

import org.springframework.http.HttpStatus;

@Service
public class CustomerServiceImpl implements ICustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerRepository customerRepository;

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

    public ResponseSaveCustomerDTO saveCustomer(SaveCustomerDTO dto) {
        logger.info("Verificando buffet con ID {}", dto.getBuffet());

        Buffet buffet = buffetRepository.findByCode(dto.getBuffet())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buffet no encontrado"));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnable(true);
        user.setCode(generateUniqueCode());
        user.setBuffet(buffet);
        user = userRepository.save(user);

        logger.info("Asignando al usuario el rol de cliente");
        Long role = 6L; // (Role 6 = Customer)
        roleRepository.assignRoleToUser(user.getId(), role);

        Customer customer = modelMapper.map(dto, Customer.class);
        customer.setUser(user);
        customer = customerRepository.save(customer);

        ResponseSaveCustomerDTO response = modelMapper.map(user, ResponseSaveCustomerDTO.class);
        response.setName(user.getName() + " " + user.getLast_name());
        response.setDocument_type(customer.getDocument_type().name());
        response.setDocument_number(customer.getDocument_number());
        response.setBuffet(buffet.getName());

        String logo = buffet.getImg();
        String nombre_buffet = buffet.getName();
        String nombre_customer = user.getName() + " " + user.getLast_name();
        String code_user = user.getCode();
        String email = user.getEmail();

        Map<String, Object> variables = Map.of(
                "logo_buffet", logo,
                "nombre_buffet", nombre_buffet,
                "nombre_usuario", nombre_customer,
                "codigo_usuario", code_user,
                "email_usuario", email);

        String correoDestino = email;
        emailService.sendEmailUsingTemplate("bienvenida_cliente", variables, correoDestino);
        logger.info("Correo enviado a: " + correoDestino);
        return response;
    }

    @Override
    @Transactional
    public ResponseSaveCustomerDTO updateCustomer(String code, UpdateCustomerDTO dto) {
        logger.info("Actualizando cliente con código: {}", code);

        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado por otro usuario");
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

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Datos de cliente no encontrados"));

        customer.setDocument_type(DocumentCustomertype.valueOf(dto.getDocument_type()));
        customer.setDocument_number(dto.getDocument_number());

        customerRepository.save(customer);

        ResponseSaveCustomerDTO response = modelMapper.map(user, ResponseSaveCustomerDTO.class);
        response.setName(customer.getUser().getName() + " " + customer.getUser().getLast_name());
        response.setDocument_type(customer.getDocument_type().name());
        response.setDocument_number(customer.getDocument_number());
        response.setBuffet(user.getBuffet().getName());

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
        responseCustomerDTO.setFullName(customer.getUser().getName() + " " + customer.getUser().getLast_name());
        responseCustomerDTO.setEmail(customer.getUser().getEmail());
        responseCustomerDTO.setCode(customer.getUser().getCode());
        responseCustomerDTO.setPhone(customer.getUser().getPhone());
        responseCustomerDTO.setAddress(customer.getUser().getAddress());
        responseCustomerDTO.setEnabled(customer.getUser().isEnable());
        responseCustomerDTO.setBuffet(customer.getUser().getBuffet().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = customer.getUser().getCreation().toLocalDateTime().format(formatter);
        responseCustomerDTO.setCreation(formattedDate);
        return responseCustomerDTO;
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