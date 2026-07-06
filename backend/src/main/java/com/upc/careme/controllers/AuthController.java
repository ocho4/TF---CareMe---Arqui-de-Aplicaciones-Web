package com.upc.careme.controllers;

import com.upc.careme.dtos.LoginRequestDTO;
import com.upc.careme.dtos.RecuperarPasswordRequestDTO;
import com.upc.careme.dtos.RegistroUsuarioDTO;
import com.upc.careme.dtos.RepostularDTO;
import com.upc.careme.dtos.ResetPasswordRequestDTO;
import com.upc.careme.dtos.UsuarioDTO;
import com.upc.careme.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public UsuarioDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }


    @PostMapping("/registro")
    public UsuarioDTO registrar(@Valid @RequestBody RegistroUsuarioDTO request) {
        return authService.registrar(request);
    }

    @PostMapping("/recuperar-password")
    public String recuperarPassword(@RequestBody RecuperarPasswordRequestDTO request) {
        return authService.recuperarPassword(request.getEmail(), request.getTelefono());
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        return authService.resetPassword(request.getToken(), request.getNuevaPassword());
    }

    @PostMapping("/repostular")
    public void repostular(@RequestBody RepostularDTO request) {
        authService.repostular(request);
    }
}