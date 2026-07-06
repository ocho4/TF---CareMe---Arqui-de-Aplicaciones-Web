package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPaciente;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "necesidades_especificas", columnDefinition = "TEXT")
    private String necesidadesEspecificas;

    @ManyToMany
    @JoinTable(
            name = "paciente_condicion",
            joinColumns = @JoinColumn(name = "id_paciente"),
            inverseJoinColumns = @JoinColumn(name = "id_condicion")
    )
    private List<CondicionMedica> condiciones;
}