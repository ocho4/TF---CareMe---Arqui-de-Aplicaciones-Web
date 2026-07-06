package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionRequestDTO {
    private Integer idServicio;
    private Integer idFamiliar;
    private Integer idCuidador;
    private Integer puntuacion;
    private String comentario;
}