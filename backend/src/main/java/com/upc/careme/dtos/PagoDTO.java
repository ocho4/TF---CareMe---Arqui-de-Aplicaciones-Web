package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Integer idPago;
    private ServicioDTO servicio;
    private Double monto;
    private String telefonoYape;
    private String codigoOperacion;
    private String metodoPago;
    private String estadoPago = "pendiente";
    private LocalDateTime fechaPago;
    private String observacion;
}