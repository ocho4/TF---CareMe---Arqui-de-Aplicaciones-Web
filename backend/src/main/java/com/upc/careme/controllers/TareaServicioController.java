package com.upc.careme.controllers;

import com.upc.careme.dtos.TareaServicioDTO;
import com.upc.careme.services.TareaServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tareas")
public class TareaServicioController {

    @Autowired
    private TareaServicioService tareaServicioService;

    @PostMapping
    public TareaServicioDTO crear(@RequestBody TareaServicioDTO dto) {
        return tareaServicioService.crear(dto);
    }

    @GetMapping("/servicio/{idServicio}")
    public List<TareaServicioDTO> listarPorServicio(@PathVariable Integer idServicio) {
        return tareaServicioService.listarPorServicio(idServicio);
    }

    @PutMapping("/{id}/completar")
    public TareaServicioDTO completar(@PathVariable Integer id) {
        return tareaServicioService.completar(id);
    }

    @PutMapping("/{id}/descompletar")
    public TareaServicioDTO descompletar(@PathVariable Integer id) {
        return tareaServicioService.descompletar(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        tareaServicioService.eliminar(id);
    }

    @PutMapping("/servicio/{idServicio}/visto-cuidador")
    public void marcarVistoCuidador(@PathVariable Integer idServicio) {
        tareaServicioService.marcarVistoPorCuidador(idServicio);
    }

    @PutMapping("/servicio/{idServicio}/visto-familiar")
    public void marcarVistoFamiliar(@PathVariable Integer idServicio) {
        tareaServicioService.marcarVistoPorFamiliar(idServicio);
    }

    @GetMapping("/no-vistos-cuidador/{idUsuario}")
    public Map<Integer, Long> noVistosCuidador(@PathVariable Integer idUsuario) {
        return tareaServicioService.noVistosPorServicioCuidador(idUsuario);
    }

    @GetMapping("/no-vistos-familiar/{idUsuario}")
    public Map<Integer, Long> noVistosFamiliar(@PathVariable Integer idUsuario) {
        return tareaServicioService.noVistosPorServicioFamiliar(idUsuario);
    }
}
