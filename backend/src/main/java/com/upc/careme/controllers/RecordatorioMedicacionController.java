package com.upc.careme.controllers;

import com.upc.careme.dtos.RecordatorioMedicacionDTO;
import com.upc.careme.dtos.RecordatorioRequestDTO;
import com.upc.careme.services.RecordatorioMedicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recordatorios-medicacion")
public class RecordatorioMedicacionController {

    @Autowired
    private RecordatorioMedicacionService service;

    @PostMapping
    public RecordatorioMedicacionDTO registrar(@RequestBody RecordatorioMedicacionDTO dto) {
        return service.insertar(dto);
    }

    @GetMapping
    public List<RecordatorioMedicacionDTO> listar() {
        return service.listar();
    }

    @GetMapping("/paciente/{idPaciente}/pendientes")
    public List<RecordatorioMedicacionDTO> listarPendientesPorPaciente(@PathVariable Integer idPaciente) {
        return service.listarPendientesPorPaciente(idPaciente);
    }

    @PostMapping("/programar")
    public String programarAlarma(@RequestBody RecordatorioRequestDTO request) {
        return service.programarAlarma(request);
    }

    @PutMapping("/{id}/tomar")
    public String marcarComoTomado(@PathVariable Integer id) {
        return service.marcarComoTomado(id);
    }
}