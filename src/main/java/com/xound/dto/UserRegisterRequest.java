package com.xound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 150, message = "El username debe tener entre 3 y 150 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]+$",
             message = "El username solo puede contener letras y numeros, sin espacios ni caracteres especiales")
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 4, max = 100, message = "La contrasena debe tener entre 4 y 100 caracteres")
    private String password;
}
