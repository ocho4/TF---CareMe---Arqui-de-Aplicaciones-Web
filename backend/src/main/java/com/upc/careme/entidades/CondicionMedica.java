package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "condiciones_medicas")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CondicionMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCondicion;

    @Column(name = "nombre_condicion", length = 100, nullable = false, unique = true)
    private String nombreCondicion;
}