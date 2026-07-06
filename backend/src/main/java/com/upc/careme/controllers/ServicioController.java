package com.upc.careme.controllers;

import com.upc.careme.dtos.ComparativaCostoDTO;
import com.upc.careme.dtos.ComparativaRequestDTO;
import com.upc.careme.dtos.CotizacionRequestDTO;
import com.upc.careme.dtos.CotizacionResponseDTO;
import com.upc.careme.dtos.ServicioDTO;
import com.upc.careme.services.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @PostMapping
    public ServicioDTO registrar(@RequestBody ServicioDTO dto) {
        return servicioService.insertar(dto);
    }

    @GetMapping
    public List<ServicioDTO> listar() {
        return servicioService.listar();
    }

    @GetMapping("/mis-servicios/{idUsuario}")
    public List<ServicioDTO> listarMisServicios(@PathVariable Integer idUsuario) {
        return servicioService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/pendientes-cuidador/{idUsuario}")
    public List<ServicioDTO> listarPendientesCuidador(@PathVariable Integer idUsuario) {
        return servicioService.listarPendientesPorCuidadorUsuario(idUsuario);
    }

    @GetMapping("/{id}")
    public ServicioDTO obtenerPorId(@PathVariable Integer id) {
        return servicioService.obtenerPorId(id);
    }

    @GetMapping("/paciente/{idPaciente}")
    public List<ServicioDTO> listarPorPaciente(@PathVariable Integer idPaciente) {
        return servicioService.listarPorPaciente(idPaciente);
    }

    @PutMapping("/{id}/confirmar")
    public ServicioDTO confirmar(@PathVariable Integer id, @RequestParam Integer idUsuarioCuidador) {
        return servicioService.confirmar(id, idUsuarioCuidador);
    }

    @PutMapping("/{id}/rechazar")
    public ServicioDTO rechazar(@PathVariable Integer id, @RequestParam Integer idUsuarioCuidador) {
        return servicioService.rechazar(id, idUsuarioCuidador);
    }

    @PutMapping("/{id}/cancelar")
    public ServicioDTO cancelar(@PathVariable Integer id) {
        return servicioService.cancelar(id);
    }

    @PutMapping("/{id}/finalizar")
    public ServicioDTO finalizar(@PathVariable Integer id) {
        return servicioService.finalizar(id);
    }

    @GetMapping("/proximos/{idUsuario}")
    public List<ServicioDTO> obtenerProximos(@PathVariable Integer idUsuario) {
        return servicioService.obtenerProximos(idUsuario);
    }

    @GetMapping("/agenda/{idUsuario}")
    public List<ServicioDTO> obtenerAgenda(
            @PathVariable Integer idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return servicioService.obtenerAgenda(idUsuario, fechaInicio, fechaFin);
    }

    @PostMapping("/cotizar")
    public CotizacionResponseDTO cotizar(@RequestBody CotizacionRequestDTO request) {
        return servicioService.cotizar(request);
    }

    @PostMapping("/comparar-costos")
    public List<ComparativaCostoDTO> compararCostos(@RequestBody ComparativaRequestDTO request) {
        return servicioService.compararCostos(request);
    }
}