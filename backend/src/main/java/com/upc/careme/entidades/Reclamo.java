package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReclamo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_servicio")
    private Servicio servicio;

    @Column(length = 150, nullable = false)
    private String asunto;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(length = 15, nullable = false)
    private String estado = "abierto";

    @Column(name = "respuesta_admin", columnDefinition = "TEXT")
    private String respuestaAdmin;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}