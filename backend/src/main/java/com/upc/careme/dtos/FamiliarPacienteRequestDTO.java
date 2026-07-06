package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FamiliarPacienteRequestDTO {
    private Integer idFamiliar;
    private Integer idPaciente;
    private String parentesco;
    private Boolean esPrincipal;
}