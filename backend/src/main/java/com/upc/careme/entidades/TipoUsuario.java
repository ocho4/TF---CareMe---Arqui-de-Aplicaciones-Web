package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_usuario")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipo;

    @Column(name = "nombre_tipo", length = 30, nullable = false, unique = true)
    private String nombreTipo;

    @Column(length = 255)
    private String descripcion;
}