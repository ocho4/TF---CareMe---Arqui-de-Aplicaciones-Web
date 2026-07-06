package com.upc.careme.controllers;

import com.upc.careme.dtos.PacienteDTO;
import com.upc.careme.dtos.PerfilPacienteRequestDTO;
import com.upc.careme.dtos.RegistrarPacienteFamiliarDTO;
import com.upc.careme.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    public PacienteDTO registrar(@RequestBody PacienteDTO dto) {
        return pacienteService.insertar(dto);
    }

    @GetMapping
    public List<PacienteDTO> listar() {
        return pacienteService.listar();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<PacienteDTO> buscarPorUsuario(@PathVariable Integer idUsuario) {
        PacienteDTO dto = pacienteService.buscarPorUsuario(idUsuario);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/perfil")
    public PacienteDTO actualizarPerfil(@PathVariable Integer id,
                                        @RequestBody PerfilPacienteRequestDTO req) {
        return pacienteService.actualizarPerfil(id, req);
    }

    @GetMapping("/familiar/{idFamiliar}")
    public List<PacienteDTO> listarPorFamiliar(@PathVariable Integer idFamiliar) {
        return pacienteService.listarPorFamiliar(idFamiliar);
    }

    @PostMapping("/familiar/{idFamiliar}")
    public PacienteDTO registrarParaFamiliar(@PathVariable Integer idFamiliar,
                                             @RequestBody RegistrarPacienteFamiliarDTO dto) {
        return pacienteService.registrarParaFamiliar(idFamiliar, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
