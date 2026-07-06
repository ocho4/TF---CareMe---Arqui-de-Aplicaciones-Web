package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequestDTO {
    private Integer idCuidador;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String especialidadRequerida;
    private Boolean esHorarioNocturno = false;
    private Boolean esFinDeSemana = false;
}
