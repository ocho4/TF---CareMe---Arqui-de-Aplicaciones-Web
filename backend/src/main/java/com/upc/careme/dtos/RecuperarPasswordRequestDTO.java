package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecuperarPasswordRequestDTO {
    private String email;
    private String telefono;
}
