package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilPacienteRequestDTO {
    private String fotoUrl;
    private String necesidadesEspecificas;
    private String nombres;
    private String apellidos;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String parentesco;
}
