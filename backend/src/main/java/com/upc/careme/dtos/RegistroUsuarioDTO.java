package com.upc.careme.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroUsuarioDTO {

    @Email(message = "Debe ingresar un correo electrónico válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    private String telefono;

    @NotNull(message = "Debe seleccionar un rol (1=familiar, 2=cuidador)")
    private Integer idTipo;

    private String motivacion;
}