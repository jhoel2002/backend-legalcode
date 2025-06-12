package com.application.claimhereweb.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.application.claimhereweb.model.entity.Customer;
import com.application.claimhereweb.model.entity.Role;
import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.entity.enumEntity.RoleName;
import com.application.claimhereweb.model.repository.CustomerRepository;
import com.application.claimhereweb.model.repository.RoleRepository;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.service.IUserService;
import com.application.claimhereweb.service.dto.ResponseInfoUserDTO;
import com.application.claimhereweb.service.dto.ResponseUserDTO;
import com.application.claimhereweb.service.dto.SaveUserDTO;
import com.application.claimhereweb.service.dto.UpdateUserEnableDTO;
import org.springframework.http.HttpStatus;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

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
}
