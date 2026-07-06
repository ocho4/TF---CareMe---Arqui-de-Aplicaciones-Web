package com.upc.careme.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private Integer idMensaje;
    private ServicioDTO servicio;
    private UsuarioDTO remitente;
    private String contenido;
    private String archivoUrl;
    private LocalDateTime fechaEnvio;
}