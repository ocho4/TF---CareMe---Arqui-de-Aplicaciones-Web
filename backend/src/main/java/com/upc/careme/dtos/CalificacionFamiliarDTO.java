package com.upc.careme.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalificacionFamiliarDTO {
    private Integer idCalificacionFamiliar;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaRegistro;
    private CuidadorDTO cuidador;
    private FamiliarDTO familiar;
}
