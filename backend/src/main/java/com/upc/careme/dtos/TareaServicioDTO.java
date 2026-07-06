package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TareaServicioDTO {
    private Integer idTarea;
    private Integer idServicio;
    private String descripcion;
    private Boolean completada;
    private LocalDateTime horaCompletado;
    private Integer creadoPor;
    private Boolean vistaPorFamiliar;
    private Boolean vistaPorCuidador;
}
