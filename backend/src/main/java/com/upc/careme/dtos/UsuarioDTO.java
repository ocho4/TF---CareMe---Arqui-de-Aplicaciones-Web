package com.upc.careme.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDTO {
    private Integer idUsuario;
    private TipoUsuarioDTO tipoUsuario;
    private String email;
    @JsonIgnore
    private String passwordHash;
    private String proveedorAuth = "local";
    private String idExterno;
    private String rol;
    private String nombres;
    private String apellidos;
    private String fotoUrl;
    private String telefono;
    @JsonIgnore
    private String tokenRecuperacion;
    @JsonIgnore
    private LocalDateTime tokenExpiracion;
    private String token;
}