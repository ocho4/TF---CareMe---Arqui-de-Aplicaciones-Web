package com.upc.careme.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @OneToOne
    @JoinColumn(name = "id_servicio", nullable = false, unique = true)
    private Servicio servicio;

    @Column(nullable = false)
    private Double monto;

    @Column(name = "telefono_yape", length = 15, nullable = false)
    private String telefonoYape;

    @Column(name = "codigo_operacion", length = 50)
    private String codigoOperacion;

    @Column(name = "metodo_pago", length = 15)
    private String metodoPago;

    @Column(name = "estado_pago", length = 15, nullable = false)
    private String estadoPago = "pendiente";

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(length = 255)
    private String observacion;
}