package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificados_cuidador")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoCuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCertificado;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(name = "archivo_url", length = 500, nullable = false)
    private String archivoUrl;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida = LocalDateTime.now();
}