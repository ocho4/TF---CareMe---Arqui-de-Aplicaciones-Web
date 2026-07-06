package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "servicios")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idServicio;

    @ManyToOne
    @JoinColumn(name = "id_familiar", nullable = false)
    private Familiar familiar;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "tipo_servicio", length = 20, nullable = false)
    private String tipoServicio = "basico";

    @Column(name = "recargo_horario", nullable = false)
    private Double recargoHorario = 0.0;

    @Column(name = "descuento_aplicado", nullable = false)
    private Double descuentoAplicado = 0.0;

    @Column(name = "costo_total", nullable = false)
    private Double costoTotal = 0.0;

    @Column(length = 15, nullable = false)
    private String estado = "SOLICITADO";
}