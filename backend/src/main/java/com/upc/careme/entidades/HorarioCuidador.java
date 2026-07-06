package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_cuidador")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioCuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHorario;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @Column(name = "dia_semana", length = 15, nullable = false)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private Boolean disponible = true;
}