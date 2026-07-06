package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "familiares")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Familiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFamiliar;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String distrito;

    @Column(name = "relacion_paciente", length = 50)
    private String relacionPaciente;
}