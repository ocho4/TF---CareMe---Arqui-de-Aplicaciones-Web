package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcesarPagoRequestDTO {
    private Integer idServicio;
    private String metodoPago;
    private String datosTransaccion;
    private String telefonoYape;
}
