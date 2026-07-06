package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones_cuidado")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionCuidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEvaluacion;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador admin;

    @Column(length = 15, nullable = false)
    private String estado = "pendiente";

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
}