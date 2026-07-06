package com.upc.careme.services;

import com.upc.careme.dtos.AprobacionDTO;
import com.upc.careme.dtos.VerificacionCuidadorDTO;
import com.upc.careme.entidades.Administrador;
import com.upc.careme.entidades.VerificacionCuidador;
import com.upc.careme.repositorios.AdministradorRepository;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.VerificacionCuidadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private VerificacionCuidadorRepository verificacionRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Transactional(readOnly = true)
    public List<VerificacionCuidadorDTO> listar(String estado) {
        List<VerificacionCuidador> lista = (estado != null && !estado.isBlank())
                ? verificacionRepository.findByEstadoOrderByFechaSolicitudDesc(estado)
                : verificacionRepository.findAllByOrderByFechaSolicitudDesc();
        return lista.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public long contarPendientes() {
        return verificacionRepository.countByEstado("pendiente");
    }

    @Transactional
    public VerificacionCuidadorDTO aprobar(Integer idVerificacion, AprobacionDTO req) {
        VerificacionCuidador v = verificacionRepository.findById(idVerificacion)
                .orElseThrow(() -> new RuntimeException("Verificación no encontrada"));

        Administrador admin = administradorRepository.findByUsuario_IdUsuario(req.getIdAdmin())
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        v.setAdmin(admin);
        v.setEstado("aprobado");
        v.setObservaciones(req.getObservaciones());
        v.setFechaRevision(LocalDateTime.now());
        verificacionRepository.save(v);

        v.getCuidador().setActivo(true);
        cuidadorRepository.save(v.getCuidador());

        return toDTO(v);
    }

    @Transactional
    public VerificacionCuidadorDTO rechazar(Integer idVerificacion, AprobacionDTO req) {
        if (req.getObservaciones() == null || req.getObservaciones().isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo del rechazo en observaciones");
        }

        VerificacionCuidador v = verificacionRepository.findById(idVerificacion)
                .orElseThrow(() -> new RuntimeException("Verificación no encontrada"));

        Administrador admin = administradorRepository.findByUsuario_IdUsuario(req.getIdAdmin())
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        v.setAdmin(admin);
        v.setEstado("rechazado");
        v.setObservaciones(req.getObservaciones());
        v.setFechaRevision(LocalDateTime.now());
        verificacionRepository.save(v);

        v.getCuidador().setActivo(false);
        cuidadorRepository.save(v.getCuidador());

        return toDTO(v);
    }

    @Transactional(readOnly = true)
    public VerificacionCuidadorDTO obtenerEstadoCuidador(Integer idCuidador) {
        return verificacionRepository
                .findTopByCuidadorIdCuidadorOrderByFechaSolicitudDesc(idCuidador)
                .map(this::toDTO)
                .orElse(null);
    }

    private VerificacionCuidadorDTO toDTO(VerificacionCuidador v) {
        VerificacionCuidadorDTO dto = new VerificacionCuidadorDTO();
        dto.setIdVerificacion(v.getIdVerificacion());
        dto.setIdCuidador(v.getCuidador().getIdCuidador());
        dto.setNombresCuidador(v.getCuidador().getUsuario().getNombres());
        dto.setApellidosCuidador(v.getCuidador().getUsuario().getApellidos());
        dto.setEmailCuidador(v.getCuidador().getUsuario().getEmail());
        dto.setEspecialidadCuidador(v.getCuidador().getEspecialidad());
        dto.setUbicacionCuidador(v.getCuidador().getUbicacion());
        dto.setTarifaBase(v.getCuidador().getTarifaBase());
        dto.setEstado(v.getEstado());
        dto.setMotivacion(v.getMotivacion());
        dto.setObservaciones(v.getObservaciones());
        dto.setFechaSolicitud(v.getFechaSolicitud());
        dto.setFechaRevision(v.getFechaRevision());
        return dto;
    }
}
