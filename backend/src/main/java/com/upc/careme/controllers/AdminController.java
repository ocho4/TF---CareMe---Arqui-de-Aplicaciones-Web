package com.upc.careme.controllers;

import com.upc.careme.dtos.AprobacionDTO;
import com.upc.careme.dtos.VerificacionCuidadorDTO;
import com.upc.careme.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/verificaciones")
    public List<VerificacionCuidadorDTO> listar(@RequestParam(required = false) String estado) {
        return adminService.listar(estado);
    }

    @GetMapping("/verificaciones/pendientes/count")
    public Map<String, Long> contarPendientes() {
        return Map.of("total", adminService.contarPendientes());
    }

    @PutMapping("/verificaciones/{id}/aprobar")
    public VerificacionCuidadorDTO aprobar(@PathVariable Integer id,
                                           @RequestBody AprobacionDTO dto) {
        return adminService.aprobar(id, dto);
    }

    @PutMapping("/verificaciones/{id}/rechazar")
    public VerificacionCuidadorDTO rechazar(@PathVariable Integer id,
                                            @RequestBody AprobacionDTO dto) {
        return adminService.rechazar(id, dto);
    }

    @GetMapping("/verificaciones/cuidador/{idCuidador}")
    public VerificacionCuidadorDTO estadoCuidador(@PathVariable Integer idCuidador) {
        return adminService.obtenerEstadoCuidador(idCuidador);
    }
}
