package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionCuidadoDTO {
    private Integer idEvaluacion;
    private CuidadorDTO cuidador;
    private UsuarioDTO usuario;
    private AdministradorDTO admin;
    private String estado = "pendiente";
    private String observaciones;
    private LocalDateTime fechaRevision;
    private LocalDateTime fechaSolicitud;
}