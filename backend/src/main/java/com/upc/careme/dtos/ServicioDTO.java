package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {
    private Integer idServicio;
    private FamiliarDTO familiar;
    private CuidadorDTO cuidador;
    private PacienteDTO paciente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCancelacion;
    private String tipoServicio = "basico";
    private Double recargoHorario = 0.0;
    private Double descuentoAplicado = 0.0;
    private Double costoTotal = 0.0;
    private String estado = "SOLICITADO";
    private Boolean pagado     = false;
    private Boolean calificado = false;
}