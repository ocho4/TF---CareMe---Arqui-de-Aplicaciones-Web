package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPacienteFamiliarDTO {
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String fotoUrl;
    private LocalDate fechaNacimiento;
    private String necesidadesEspecificas;
    private String parentesco;
}
