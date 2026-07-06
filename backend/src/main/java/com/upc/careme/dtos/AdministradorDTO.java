package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorDTO {
    private Integer idAdmin;
    private UsuarioDTO usuario;
    private String nivelAcceso = "total";
}