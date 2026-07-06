package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_admin")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReporte;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administrador admin;

    @Column(name = "tipo_reporte", length = 50, nullable = false)
    private String tipoReporte;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String parametros;

    @Column(name = "resultado_url", length = 500)
    private String resultadoUrl;
}