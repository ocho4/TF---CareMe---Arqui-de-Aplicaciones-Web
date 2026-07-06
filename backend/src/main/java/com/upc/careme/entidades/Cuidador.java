package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cuidadores")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCuidador;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(length = 100)
    private String especialidad;

    @Column(length = 100, nullable = false)
    private String ubicacion;

    @Column(name = "disponibilidad_texto", length = 255)
    private String disponibilidadTexto;

    @Column(name = "tarifa_base", nullable = false)
    private Double tarifaBase = 0.0;

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio = 0.0;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean observado = false;
    

    @ManyToMany
    @JoinTable(
            name = "cuidador_condicion",
            joinColumns = @JoinColumn(name = "id_cuidador"),
            inverseJoinColumns = @JoinColumn(name = "id_condicion")
    )
    private List<CondicionMedica> condiciones;
}