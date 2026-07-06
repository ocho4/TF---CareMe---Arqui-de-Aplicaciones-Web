package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConsejoRequestDTO {
    private Integer idUsuario;
    private String titulo;
    private String contenido;
    private String categoria;
}