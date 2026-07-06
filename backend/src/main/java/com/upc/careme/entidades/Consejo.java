package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consejos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Consejo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsejo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(length = 150, nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(length = 50)
    private String categoria;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;
}