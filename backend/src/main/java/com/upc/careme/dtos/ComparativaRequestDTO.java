package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComparativaRequestDTO {
    private List<Integer> idsCuidadores;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String especialidadRequerida;
    private Boolean esHorarioNocturno = false;
    private Boolean esFinDeSemana = false;
}
