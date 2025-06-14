package com.application.claimhereweb.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.claimhereweb.model.entity.User;
import com.application.claimhereweb.model.repository.UserRepository;
import com.application.claimhereweb.security.CustomUserDetails;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> opt = userRepository.findByEmail(email);

        User user = opt.orElseThrow(() ->
            new UsernameNotFoundException("Correo %s no existe en el sistema!".formatted(email)));

        return new CustomUserDetails(user);   //  ←  devolvemos nuestra clase
    }
}
