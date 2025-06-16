package com.application.claimhereweb.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.application.claimhereweb.model.entity.Buffet;
import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.Role;
import com.application.claimhereweb.model.entity.SimplePageResponse;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.RoleName;
import com.application.claimhereweb.model.repository.BuffetRepository;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.IUserService;
import com.application.claimhereweb.service.dto.ResponseInfoUserDTO;
import com.application.claimhereweb.service.dto.ResponseUserDTO;
import com.application.claimhereweb.service.dto.SaveUserDTO;
import com.application.claimhereweb.service.dto.UpdateUserEnableDTO;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    BuffetRepository buffetRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public SimplePageResponse<ResponseUserDTO> findAllAdmin(Pageable pageable, String codeBuffet) {
        logger.info("Listando usuarios registrados");
        List<String> roles = List.of("ROLE_OTHERS", "ROLE_COORDINATOR", "ROLE_ADMINISTRATOR", "ROLE_LEGAL_ASSISTANT");
        Page<User> users = userRepository.findByRolesInAndBuffetCode(roles, codeBuffet, pageable);
        Page<ResponseUserDTO> dtoPage = users.map(this::responseFullUser);
        return new SimplePageResponse<>(dtoPage);
    }

    private ResponseUserDTO responseFullUser(User user) {
        ResponseUserDTO dto = new ResponseUserDTO();
        dto.setId(user.getId());
        dto.setCode(user.getCode());
        dto.setName(user.getName());
        dto.setLast_name(user.getLast_name());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());

        String roleName = user.getRoles().stream()
                .findFirst()
                .map(Role::getName)
                .orElse("");
        dto.setRole(roleName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = user.getCreation().toLocalDateTime().format(formatter);
        dto.setCreation(formattedDate);
        return dto;
    }

    @Override
    @Transactional
    public ResponseUserDTO saveAdmin(SaveUserDTO dto, String codeBuffet) {
        logger.info("Verificando buffet con ID {}", codeBuffet);

        Buffet buffet = buffetRepository.findByCode(codeBuffet)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buffet no encontrado"));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnable(true);
        user.setCode(generateUniqueCode());
        user.setBuffet(buffet);
        user = userRepository.save(user);

        logger.info("Asignando al usuario el rol de cliente");
        Role role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role no encontrado"));
        roleRepository.assignRoleToUser(user.getId(), role.getId());

        ResponseUserDTO response = modelMapper.map(user, ResponseUserDTO.class);
        response.setRole(role.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = user.getCreation().toLocalDateTime().format(formatter);
        response.setCreation(formattedDate);

        return response;
    }

    @Override
    @Transactional
    public void updateUserEnableStatus(UpdateUserEnableDTO dto) {
        User user = userRepository.findByCode(dto.getCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setEnable(dto.isEnable());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public ResponseInfoUserDTO getUserByCode(String code) {
        User user = userRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Datos del cliente no encontrados"));

        ResponseInfoUserDTO response = new ResponseInfoUserDTO();
        response.setCode(user.getCode());
        response.setEmail(user.getEmail());
        response.setFullName(user.getName() + " " + user.getLast_name());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setDocument_type(customer.getDocument_type().name());
        response.setDocument_number(customer.getDocument_number());
        response.setBuffet(user.getBuffet().getName());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseUserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::responseUser)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ResponseUserDTO saveByAdmin(SaveUserDTO user) {
        User saved = prepareUser(user, RoleName.ROLE_ADMINISTRATOR);
        return responseUser(saved);
    }

    @Transactional
    @Override
    public ResponseUserDTO save(SaveUserDTO user, RoleName role) {
        User saved = prepareUser(user, role);
        return responseUser(saved);
    }

    private User prepareUser(SaveUserDTO user, RoleName roleName) {
        User userModel = modelMapper.map(user, User.class);
        Set<Role> roles = new HashSet<>();
        Optional<Role> optionalRoleMod = roleRepository.findByName(roleName.name());
        optionalRoleMod.ifPresent(roles::add);
        userModel.setRoles(roles);
        userModel.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(userModel);
        return savedUser;
    }

    private ResponseUserDTO responseUser(User user) {
        ResponseUserDTO responseUserDTO = modelMapper.map(user, ResponseUserDTO.class);
        responseUserDTO.setRole(user.getFirstRoleName());
        return responseUserDTO;
    }

    @Override
    public boolean existsByUsername(String email) {
        return userRepository.existsByEmail(email);
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
