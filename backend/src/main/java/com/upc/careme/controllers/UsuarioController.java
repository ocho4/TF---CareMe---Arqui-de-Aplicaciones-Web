package com.upc.careme.controllers;

import com.upc.careme.dtos.UsuarioDTO;
import com.upc.careme.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioDTO registrar(@RequestBody UsuarioDTO dto) {
        return usuarioService.insertar(dto);
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.listar();
    }
}