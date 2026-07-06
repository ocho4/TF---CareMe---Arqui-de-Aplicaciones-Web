package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioMedicacionDTO {
    private Integer idRecordatorio;
    private PacienteDTO paciente;
    private String nombreMedicamento;
    private LocalDateTime horaProgramada;
    private Boolean tomado = false;
}