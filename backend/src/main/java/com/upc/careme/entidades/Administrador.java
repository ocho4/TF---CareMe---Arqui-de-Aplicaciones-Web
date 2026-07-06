package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administradores")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAdmin;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;
}