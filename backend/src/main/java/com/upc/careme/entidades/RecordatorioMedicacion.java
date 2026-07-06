package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorios_medicacion")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordatorioMedicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecordatorio;

    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "nombre_medicamento", length = 100, nullable = false)
    private String nombreMedicamento;

    @Column(name = "hora_programada", nullable = false)
    private LocalDateTime horaProgramada;

    @Column(nullable = false)
    private Boolean tomado = false;
}