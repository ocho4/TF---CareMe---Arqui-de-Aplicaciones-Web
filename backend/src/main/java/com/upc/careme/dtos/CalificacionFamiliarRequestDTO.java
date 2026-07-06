package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionFamiliarRequestDTO {
    private Integer idServicio;
    private Integer idCuidador;
    private Integer idFamiliar;
    private Integer puntuacion;
    private String comentario;
}
