package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoRequestDTO {
    private Integer idUsuario;
    private Integer idServicio;
    private String asunto;
    private String descripcion;
}