package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FamiliarDTO {
    private Integer idFamiliar;
    private UsuarioDTO usuario;
    private String direccion;
    private String distrito;
    private String relacionPaciente;
}