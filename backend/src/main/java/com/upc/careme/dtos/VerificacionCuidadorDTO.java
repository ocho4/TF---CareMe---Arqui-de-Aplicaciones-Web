package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificacionCuidadorDTO {
    private Integer idVerificacion;
    private Integer idCuidador;
    private String nombresCuidador;
    private String apellidosCuidador;
    private String emailCuidador;
    private String especialidadCuidador;
    private String ubicacionCuidador;
    private Double tarifaBase;
    private String estado;
    private String motivacion;
    private String observaciones;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRevision;
}
