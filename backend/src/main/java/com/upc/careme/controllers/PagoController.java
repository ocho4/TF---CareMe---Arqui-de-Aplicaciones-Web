package com.upc.careme.controllers;

import com.upc.careme.dtos.PagoDTO;
import com.upc.careme.dtos.PagoRequestDTO;
import com.upc.careme.dtos.ProcesarPagoRequestDTO;
import com.upc.careme.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping
    public PagoDTO registrar(@RequestBody PagoDTO dto) {
        return pagoService.insertar(dto);
    }

    @GetMapping
    public List<PagoDTO> listar() {
        return pagoService.listar();
    }

    @PostMapping("/yape")
    public String registrarPagoYape(@RequestBody PagoRequestDTO request) {
        return pagoService.registrarPagoYape(request);
    }

    @PostMapping("/procesar")
    public String procesar(@RequestBody ProcesarPagoRequestDTO request) {
        return pagoService.procesar(request);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<PagoDTO> listarPorUsuario(@PathVariable Integer idUsuario) {
        return pagoService.listarPorUsuario(idUsuario);
    }
}