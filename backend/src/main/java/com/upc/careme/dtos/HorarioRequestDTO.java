package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioRequestDTO {
    private Integer idCuidador;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}