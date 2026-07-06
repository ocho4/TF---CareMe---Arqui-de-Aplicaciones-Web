package com.upc.careme.entidades;

import lombok.*;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FamiliarPacienteId implements Serializable {
    private Integer familiar;
    private Integer paciente;
}