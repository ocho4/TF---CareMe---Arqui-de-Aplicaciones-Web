package com.upc.careme.controllers;

import com.upc.careme.dtos.FamiliarDTO;
import com.upc.careme.dtos.FamiliarRequestDTO;
import com.upc.careme.dtos.PerfilFamiliarRequestDTO;
import com.upc.careme.services.FamiliarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/familiares")
public class FamiliarController {

    @Autowired
    private FamiliarService familiarService;

    @PostMapping
    public FamiliarDTO registrar(@RequestBody FamiliarDTO dto) {
        return familiarService.insertar(dto);
    }

    @GetMapping
    public List<FamiliarDTO> listar() {
        return familiarService.listar();
    }

    @GetMapping("/usuario/{idUsuario}")
    public FamiliarDTO buscarPorUsuario(@PathVariable Integer idUsuario) {
        return familiarService.buscarPorUsuario(idUsuario);
    }

    @PostMapping("/completar-perfil")
    public FamiliarDTO completarPerfil(@RequestBody FamiliarRequestDTO request) {
        return familiarService.completarPerfil(request);
    }

    @PutMapping("/{id}/perfil")
    public FamiliarDTO actualizarPerfil(@PathVariable Integer id,
                                        @RequestBody PerfilFamiliarRequestDTO req) {
        return familiarService.actualizarPerfil(id, req);
    }
}