package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeRequestDTO {
    private Integer idServicio;
    private Integer idRemitente;
    private String contenido;
    private String archivoUrl;
}