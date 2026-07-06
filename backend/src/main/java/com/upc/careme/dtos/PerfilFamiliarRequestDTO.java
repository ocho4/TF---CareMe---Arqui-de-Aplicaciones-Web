package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilFamiliarRequestDTO {
    private String fotoUrl;
    private String direccion;
    private String distrito;
}
