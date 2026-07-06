package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificaciones_familiar")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionFamiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCalificacionFamiliar;

    @OneToOne
    @JoinColumn(name = "id_servicio", nullable = false, unique = true)
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @ManyToOne
    @JoinColumn(name = "id_familiar", nullable = false)
    private Familiar familiar;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
