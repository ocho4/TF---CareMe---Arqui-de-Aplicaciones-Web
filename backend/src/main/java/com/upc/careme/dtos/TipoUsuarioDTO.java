package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuarioDTO {
    private Integer idTipo;
    private String nombreTipo;
    private String descripcion;
}