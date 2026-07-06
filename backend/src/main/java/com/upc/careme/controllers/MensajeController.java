package com.upc.careme.controllers;

import com.upc.careme.dtos.MensajeDTO;
import com.upc.careme.dtos.MensajeRequestDTO;
import com.upc.careme.services.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping
    public MensajeDTO registrar(@RequestBody MensajeDTO dto) {
        return mensajeService.insertar(dto);
    }

    @GetMapping
    public List<MensajeDTO> listar() {
        return mensajeService.listar();
    }

    @PostMapping("/enviar")
    public String enviarMensaje(@RequestBody MensajeRequestDTO request) {
        return mensajeService.enviarMensaje(request);
    }

    @GetMapping("/servicio/{idServicio}")
    public List<MensajeDTO> historialPorServicio(@PathVariable Integer idServicio) {
        return mensajeService.historialPorServicio(idServicio);
    }

    @PutMapping("/servicio/{idServicio}/leer/{idUsuario}")
    public void marcarLeidosServicio(@PathVariable Integer idServicio,
                                     @PathVariable Integer idUsuario) {
        mensajeService.marcarLeidosServicio(idServicio, idUsuario);
    }

    @GetMapping("/no-leidos-por-servicio/{idUsuario}")
    public Map<Integer, Long> noLeidosPorServicio(@PathVariable Integer idUsuario) {
        return mensajeService.noLeidosPorServicio(idUsuario);
    }
}