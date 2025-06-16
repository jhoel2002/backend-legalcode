package com.application.claimhereweb.service.dto;

import com.application.claimhereweb.validation.ExistsByEmail;
import com.application.claimhereweb.validation.IsRequired;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveUserDTO {

    @IsRequired
    private String name;

    @IsRequired
    private String last_name;

    @ExistsByEmail
    @IsRequired
    @Email(message = "debe ser un correo electrónico válido")
    private String email;

    @IsRequired
    @Size(min = 8, message = "debe tener minimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$", message = "debe tener al menos una mayúscula, una minúscula y un número")
    private String password;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "debe ser un número telefónico válido")
    @IsRequired
    private String phone;

    @IsRequired
    private String address;

    @IsRequired
    private String role;
}
