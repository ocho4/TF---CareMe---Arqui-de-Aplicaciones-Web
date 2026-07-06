package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "proveedor_auth", length = 20, nullable = false)
    private String proveedorAuth = "local";

    @Column(name = "id_externo", length = 255)
    private String idExterno;

    @Column(length = 10, nullable = false)
    private String rol;

    @Column(length = 100, nullable = false)
    private String nombres;

    @Column(length = 100, nullable = false)
    private String apellidos;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(length = 15)
    private String telefono;

    @Column(name = "token_recuperacion", length = 255)
    private String tokenRecuperacion;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;
}