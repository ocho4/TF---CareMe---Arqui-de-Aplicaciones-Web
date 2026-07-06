package com.upc.careme.controllers;

import com.upc.careme.dtos.CalificacionDTO;
import com.upc.careme.dtos.CalificacionFamiliarDTO;
import com.upc.careme.dtos.CalificacionFamiliarRequestDTO;
import com.upc.careme.dtos.CalificacionRequestDTO;
import com.upc.careme.services.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    @PostMapping
    public CalificacionDTO registrar(@RequestBody CalificacionDTO dto) {
        return calificacionService.insertar(dto);
    }

    @GetMapping
    public List<CalificacionDTO> listar() {
        return calificacionService.listar();
    }

    @PostMapping("/resena")
    public String registrarResena(@RequestBody CalificacionRequestDTO request) {
        return calificacionService.registrarResena(request);
    }

    @GetMapping("/cuidador/{idCuidador}")
    public List<CalificacionDTO> listarPorCuidador(@PathVariable Integer idCuidador) {
        return calificacionService.listarPorCuidador(idCuidador);
    }

    @PostMapping("/resena-familiar")
    public String registrarResenaFamiliar(@RequestBody CalificacionFamiliarRequestDTO request) {
        return calificacionService.registrarResenaFamiliar(request);
    }

    @GetMapping("/familiar/{idFamiliar}")
    public List<CalificacionFamiliarDTO> listarPorFamiliar(@PathVariable Integer idFamiliar) {
        return calificacionService.listarPorFamiliar(idFamiliar);
    }

    @GetMapping("/resena-familiar/ids-por-cuidador/{idCuidador}")
    public List<Integer> idsServiciosCalificadosPorCuidador(@PathVariable Integer idCuidador) {
        return calificacionService.listarIdsServiciosCalificadosPorCuidador(idCuidador);
    }
}