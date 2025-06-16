package com.application.claimhereweb.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.application.claimhereweb.service.dto.ReponseSaveBuffetDTO;
import com.application.claimhereweb.service.dto.SaveBuffetDTO;

public interface IBuffetService {

    // Metodo para guardar buffet
    public ReponseSaveBuffetDTO saveBuffet(SaveBuffetDTO SaveBuffetDTO);

    // Metodo para inactivar o activar buffet
    public void updateBuffetEnableStatus(String code, boolean enable);

    // Metodo para cargar en el S3 y actualizar en la BD el logo del buffet
    public String uploadBuffetLogo(MultipartFile file, String buffetCode);

    public ReponseSaveBuffetDTO saveBuffetWithLogo(SaveBuffetDTO dto, MultipartFile file);

    public List<String> getTypeCasesByBuffetCode(String code);
}
