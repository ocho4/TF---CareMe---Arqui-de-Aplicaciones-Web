package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "familiar_paciente")
@IdClass(FamiliarPacienteId.class)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FamiliarPaciente {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_familiar", nullable = false)
    private Familiar familiar;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(length = 50)
    private String parentesco;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = true;
}