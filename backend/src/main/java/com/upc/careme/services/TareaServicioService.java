package com.upc.careme.services;

import com.upc.careme.dtos.TareaServicioDTO;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.entidades.TareaServicio;
import com.upc.careme.repositorios.ServicioRepository;
import com.upc.careme.repositorios.TareaServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TareaServicioService {

    @Autowired
    private TareaServicioRepository tareaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    public TareaServicioDTO crear(TareaServicioDTO dto) {
        Servicio servicio = servicioRepository.findById(dto.getIdServicio())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        TareaServicio tarea = new TareaServicio();
        tarea.setServicio(servicio);
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setCompletada(false);
        tarea.setCreadoPor(dto.getCreadoPor());
        tarea.setVistaPorFamiliar(true);   // familiar la creó, ya la vio
        tarea.setVistaPorCuidador(false);  // cuidador aún no la ve
        tarea = tareaRepository.save(tarea);
        return toDTO(tarea);
    }

    public List<TareaServicioDTO> listarPorServicio(Integer idServicio) {
        return tareaRepository.findByServicioIdServicioOrderByIdTareaAsc(idServicio)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TareaServicioDTO completar(Integer idTarea) {
        TareaServicio tarea = tareaRepository.findById(idTarea)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tarea.setCompletada(true);
        tarea.setHoraCompletado(LocalDateTime.now());
        tarea.setVistaPorFamiliar(false);  // familiar aún no ve la completación
        tareaRepository.save(tarea);
        return toDTO(tarea);
    }

    public TareaServicioDTO descompletar(Integer idTarea) {
        TareaServicio tarea = tareaRepository.findById(idTarea)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        tarea.setCompletada(false);
        tarea.setHoraCompletado(null);
        tarea.setVistaPorFamiliar(true);   // vuelve a pendiente, no hay novedad para familiar
        tareaRepository.save(tarea);
        return toDTO(tarea);
    }

    public void eliminar(Integer idTarea) {
        tareaRepository.deleteById(idTarea);
    }

    @Transactional
    public void marcarVistoPorCuidador(Integer idServicio) {
        tareaRepository.marcarVistoPorCuidador(idServicio);
    }

    @Transactional
    public void marcarVistoPorFamiliar(Integer idServicio) {
        tareaRepository.marcarVistoPorFamiliar(idServicio);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> noVistosPorServicioCuidador(Integer idUsuario) {
        List<Object[]> rows = tareaRepository.countNuevasPorServicioCuidador(idUsuario);
        Map<Integer, Long> mapa = new HashMap<>();
        for (Object[] row : rows) mapa.put((Integer) row[0], (Long) row[1]);
        return mapa;
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> noVistosPorServicioFamiliar(Integer idUsuario) {
        List<Object[]> rows = tareaRepository.countCompletadasPorServicioFamiliar(idUsuario);
        Map<Integer, Long> mapa = new HashMap<>();
        for (Object[] row : rows) mapa.put((Integer) row[0], (Long) row[1]);
        return mapa;
    }

    private TareaServicioDTO toDTO(TareaServicio t) {
        TareaServicioDTO dto = new TareaServicioDTO();
        dto.setIdTarea(t.getIdTarea());
        dto.setIdServicio(t.getServicio().getIdServicio());
        dto.setDescripcion(t.getDescripcion());
        dto.setCompletada(Boolean.TRUE.equals(t.getCompletada()));
        dto.setHoraCompletado(t.getHoraCompletado());
        dto.setCreadoPor(t.getCreadoPor());
        dto.setVistaPorFamiliar(Boolean.TRUE.equals(t.getVistaPorFamiliar()));
        dto.setVistaPorCuidador(Boolean.TRUE.equals(t.getVistaPorCuidador()));
        return dto;
    }
}
