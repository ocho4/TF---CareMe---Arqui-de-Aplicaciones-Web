package com.upc.careme.controllers;

import com.upc.careme.dtos.NotificacionDTO;
import com.upc.careme.services.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping("/cuidador/{idUsuario}")
    public List<NotificacionDTO> obtenerParaCuidador(@PathVariable Integer idUsuario) {
        return notificacionService.obtenerParaCuidador(idUsuario);
    }

    @GetMapping("/familiar/{idUsuario}")
    public List<NotificacionDTO> obtenerParaFamiliar(@PathVariable Integer idUsuario) {
        return notificacionService.obtenerParaFamiliar(idUsuario);
    }
}
