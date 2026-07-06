package com.upc.careme.controllers;

import com.upc.careme.dtos.TipoUsuarioDTO;
import com.upc.careme.services.TipoUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-usuario")
public class TipoUsuarioController {

    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    @PostMapping
    public TipoUsuarioDTO registrar(@RequestBody TipoUsuarioDTO dto) {
        return tipoUsuarioService.insertar(dto);
    }

    @GetMapping
    public List<TipoUsuarioDTO> listar() {
        return tipoUsuarioService.listar();
    }
}