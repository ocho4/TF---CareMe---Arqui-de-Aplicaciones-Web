package com.upc.careme.controllers;

import com.upc.careme.dtos.CondicionMedicaDTO;
import com.upc.careme.services.CondicionMedicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/condiciones-medicas")
public class CondicionMedicaController {

    @Autowired
    private CondicionMedicaService condicionMedicaService;

    @PostMapping
    public CondicionMedicaDTO registrar(@RequestBody CondicionMedicaDTO dto) {
        return condicionMedicaService.insertar(dto);
    }

    @GetMapping
    public List<CondicionMedicaDTO> listar() {
        return condicionMedicaService.listar();
    }
}