package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoRequestDTO {
    private Integer idCuidador;
    private String nombre;
    private String archivoUrl;
}