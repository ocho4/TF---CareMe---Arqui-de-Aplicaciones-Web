package com.upc.careme.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {
    private String token;
    private String nuevaPassword;
}
