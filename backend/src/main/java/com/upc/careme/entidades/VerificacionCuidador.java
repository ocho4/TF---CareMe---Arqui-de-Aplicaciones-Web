package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verificaciones_cuidador")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VerificacionCuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVerificacion;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = true)
    private Administrador admin;

    @Column(length = 15, nullable = false)
    private String estado = "pendiente";

    @Column(columnDefinition = "TEXT")
    private String motivacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;
}
