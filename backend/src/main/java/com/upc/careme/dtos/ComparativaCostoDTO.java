package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComparativaCostoDTO {
    private CuidadorDTO cuidador;
    private Long horas;
    private Double tarifaBase;
    private Double costoBase;
    private Double recargoNocturno;
    private Double recargoFinDeSemana;
    private Double recargoEspecialidad;
    private Double descuentoLargaDuracion;
    private Double costoTotal;
}
