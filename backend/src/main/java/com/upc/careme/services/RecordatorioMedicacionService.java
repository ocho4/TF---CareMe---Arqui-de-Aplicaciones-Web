package com.upc.careme.services;

import com.upc.careme.dtos.RecordatorioMedicacionDTO;
import com.upc.careme.dtos.RecordatorioRequestDTO;
import com.upc.careme.entidades.Paciente;
import com.upc.careme.entidades.RecordatorioMedicacion;
import com.upc.careme.repositorios.PacienteRepository;
import com.upc.careme.repositorios.RecordatorioMedicacionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordatorioMedicacionService {

    @Autowired
    private RecordatorioMedicacionRepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ModelMapper modelMapper;


    public RecordatorioMedicacionDTO insertar(RecordatorioMedicacionDTO dto) {
        RecordatorioMedicacion entidad = modelMapper.map(dto, RecordatorioMedicacion.class);
        entidad = repository.save(entidad);
        return modelMapper.map(entidad, RecordatorioMedicacionDTO.class);
    }

    public List<RecordatorioMedicacionDTO> listar() {
        return repository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, RecordatorioMedicacionDTO.class))
                .toList();
    }

    public List<RecordatorioMedicacionDTO> listarPendientesPorPaciente(Integer idPaciente) {
        return repository.findByPacienteIdPacienteAndTomadoFalse(idPaciente)
                .stream()
                .map(entidad -> modelMapper.map(entidad, RecordatorioMedicacionDTO.class))
                .toList();
    }


    public String programarAlarma(RecordatorioRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));

        RecordatorioMedicacion recordatorio = new RecordatorioMedicacion();
        recordatorio.setPaciente(paciente);
        recordatorio.setNombreMedicamento(request.getNombreMedicamento());
        recordatorio.setHoraProgramada(request.getHoraProgramada());
        recordatorio.setTomado(false);

        repository.save(recordatorio);

        return "Alarma programada: Se debe administrar '" + request.getNombreMedicamento() + "' al paciente " + paciente.getUsuario().getNombres();
    }

    public String marcarComoTomado(Integer idRecordatorio) {
        RecordatorioMedicacion recordatorio = repository.findById(idRecordatorio)
                .orElseThrow(() -> new IllegalArgumentException("El recordatorio especificado no existe."));

        if (recordatorio.getTomado()) {
            return "Este medicamento ya había sido marcado como tomado previamente.";
        }

        recordatorio.setTomado(true);
        repository.save(recordatorio);

        return "¡Check! El medicamento '" + recordatorio.getNombreMedicamento() + "' ha sido administrado con éxito.";
    }
}