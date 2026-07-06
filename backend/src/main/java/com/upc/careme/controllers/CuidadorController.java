package com.upc.careme.controllers;

import com.upc.careme.dtos.CuidadorDTO;
import com.upc.careme.dtos.PerfilCuidadorRequestDTO;
import com.upc.careme.services.CuidadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuidadores")
public class CuidadorController {

    @Autowired
    private CuidadorService cuidadorService;

    @PostMapping
    public CuidadorDTO registrar(@RequestBody CuidadorDTO dto) {
        return cuidadorService.insertar(dto);
    }

    @GetMapping
    public List<CuidadorDTO> listar() {
        return cuidadorService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuidadorDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(cuidadorService.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CuidadorDTO> buscarPorUsuario(@PathVariable Integer idUsuario) {
        CuidadorDTO dto = cuidadorService.buscarPorUsuario(idUsuario);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/perfil")
    public CuidadorDTO actualizarPerfil(@PathVariable Integer id,
                                        @RequestBody PerfilCuidadorRequestDTO req) {
        return cuidadorService.actualizarPerfil(id, req);
    }

    @GetMapping("/buscar/especialidad")
    public List<CuidadorDTO> buscarPorEspecialidad(@RequestParam String especialidad) {
        return cuidadorService.buscarPorEspecialidad(especialidad);
    }

    @GetMapping("/buscar/ubicacion")
    public List<CuidadorDTO> buscarPorUbicacion(@RequestParam String ubicacion) {
        return cuidadorService.buscarPorUbicacion(ubicacion);
    }

    @GetMapping("/buscar")
    public List<CuidadorDTO> buscarPorFiltros(
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String disponibilidad) {
        return cuidadorService.buscarPorFiltros(ubicacion, especialidad, disponibilidad);
    }

    @GetMapping("/condicion-medica")
    public ResponseEntity<Object> buscarPorCondicionMedica(@RequestParam String condicion) {
        List<CuidadorDTO> resultados = cuidadorService.buscarPorCondicionMedica(condicion);
        if (resultados.isEmpty()) {
            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("mensaje", "No se encontraron cuidadores con experiencia en '" + condicion + "'.");
            respuesta.put("sugerencia", "Considera buscar por especialidades relacionadas: cuidado general, enfermería o geriatría.");
            respuesta.put("resultados", List.of());
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.ok(resultados);
    }
}