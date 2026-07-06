package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioRequestDTO {
    private Integer idPaciente;
    private String nombreMedicamento;
    private LocalDateTime horaProgramada;
}