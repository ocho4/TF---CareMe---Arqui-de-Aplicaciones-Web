package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FamiliarRequestDTO {
    private Integer idUsuario;
    private String direccion;
    private String distrito;
}