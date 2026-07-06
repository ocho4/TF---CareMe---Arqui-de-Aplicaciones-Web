package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuidadores_favoritos",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_familiar", "id_cuidador"}))
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CuidadorFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFavorito;

    @ManyToOne
    @JoinColumn(name = "id_familiar", nullable = false)
    private Familiar familiar;

    @ManyToOne
    @JoinColumn(name = "id_cuidador", nullable = false)
    private Cuidador cuidador;

    @Column(name = "fecha_guardado", nullable = false)
    private LocalDateTime fechaGuardado = LocalDateTime.now();
}
