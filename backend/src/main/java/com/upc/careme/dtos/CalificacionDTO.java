package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDTO {
    private Integer idCalificacion;
    private ServicioDTO servicio;
    private FamiliarDTO familiar;
    private CuidadorDTO cuidador;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaRegistro;
}