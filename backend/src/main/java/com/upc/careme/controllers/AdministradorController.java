package com.upc.careme.controllers;

import com.upc.careme.dtos.AdministradorDTO;
import com.upc.careme.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @PostMapping
    public AdministradorDTO registrar(@RequestBody AdministradorDTO dto) {
        return administradorService.insertar(dto);
    }

    @GetMapping
    public List<AdministradorDTO> listar() {
        return administradorService.listar();
    }
}