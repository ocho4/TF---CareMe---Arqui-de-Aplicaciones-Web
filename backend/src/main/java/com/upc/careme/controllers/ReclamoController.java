package com.upc.careme.controllers;

import com.upc.careme.dtos.ReclamoRequestDTO;
import com.upc.careme.services.ReclamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reclamos")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @PostMapping("/abrir")
    public String abrirReclamo(@RequestBody ReclamoRequestDTO request) {
        return reclamoService.registrarReclamo(request);
    }
}