package com.application.claimhereweb.service;

import java.util.List;

import com.application.claimhereweb.model.entity.enumEntity.RoleName;
import com.application.claimhereweb.service.dto.ResponseInfoUserDTO;
import com.application.claimhereweb.service.dto.ResponseUserDTO;
import com.application.claimhereweb.service.dto.SaveUserDTO;
import com.application.claimhereweb.service.dto.UpdateUserEnableDTO;

public interface IUserService {

    // Metodo para inactivar o activar el usuario
    public void updateUserEnableStatus(UpdateUserEnableDTO dto);

    // Metodo para buscar usuario por codigo
    public ResponseInfoUserDTO getUserByCode(String code);

    List<ResponseUserDTO> findAll();

    ResponseUserDTO saveByAdmin(SaveUserDTO user);

    ResponseUserDTO save(SaveUserDTO user, RoleName role);

    boolean existsByUsername(String email);
}
