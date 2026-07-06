package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMensaje;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @ManyToOne
    @JoinColumn(name = "id_remitente", nullable = false)
    private Usuario remitente;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean leido = false;
}