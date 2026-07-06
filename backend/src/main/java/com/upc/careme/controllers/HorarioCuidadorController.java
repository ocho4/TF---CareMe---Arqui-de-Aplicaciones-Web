package com.upc.careme.controllers;

import com.upc.careme.dtos.HorarioRequestDTO;
import com.upc.careme.services.HorarioCuidadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horarios")
public class HorarioCuidadorController {

    @Autowired
    private HorarioCuidadorService horarioService;

    @PostMapping("/registrar")
    public String registrarHorario(@RequestBody HorarioRequestDTO request) {
        return horarioService.registrarHorario(request);
    }
}