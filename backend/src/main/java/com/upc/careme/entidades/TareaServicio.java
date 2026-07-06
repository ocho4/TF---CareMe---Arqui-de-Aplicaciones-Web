package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas_servicio")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TareaServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTarea;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(length = 255, nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Boolean completada = false;

    @Column(name = "hora_completado")
    private LocalDateTime horaCompletado;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "vista_por_familiar", nullable = false)
    private Boolean vistaPorFamiliar = true;

    @Column(name = "vista_por_cuidador", nullable = false)
    private Boolean vistaPorCuidador = true;
}