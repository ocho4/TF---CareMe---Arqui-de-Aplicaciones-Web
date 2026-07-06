package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCuidadorRequestDTO {
    private String fotoUrl;
    private String ubicacion;
    private String especialidad;
    private String disponibilidadTexto;
    private Double tarifaBase;
}
