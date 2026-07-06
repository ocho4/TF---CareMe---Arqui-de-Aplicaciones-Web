package com.upc.careme.services;

import com.upc.careme.dtos.CuidadorDTO;
import com.upc.careme.dtos.PerfilCuidadorRequestDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuidadorService {

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CuidadorDTO insertar(CuidadorDTO dto) {
        Cuidador entidad = modelMapper.map(dto, Cuidador.class);
        entidad = cuidadorRepository.save(entidad);
        return modelMapper.map(entidad, CuidadorDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CuidadorDTO> listar() {
        return cuidadorRepository.findActivosConPerfilCompleto()
                .stream()
                .map(entidad -> modelMapper.map(entidad, CuidadorDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public CuidadorDTO buscarPorId(Integer id) {
        return cuidadorRepository.findById(id)
                .map(c -> modelMapper.map(c, CuidadorDTO.class))
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado"));
    }

    @Transactional(readOnly = true)
    public CuidadorDTO buscarPorUsuario(Integer idUsuario) {
        Cuidador c = cuidadorRepository.findByUsuario_IdUsuario(idUsuario).orElse(null);
        if (c == null) return null;
        return modelMapper.map(c, CuidadorDTO.class);
    }

    @Transactional
    public CuidadorDTO actualizarPerfil(Integer idCuidador, PerfilCuidadorRequestDTO req) {
        Cuidador cuidador = cuidadorRepository.findById(idCuidador)
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado"));

        if (req.getUbicacion()           != null) cuidador.setUbicacion(req.getUbicacion());
        if (req.getEspecialidad()        != null) cuidador.setEspecialidad(req.getEspecialidad());
        if (req.getDisponibilidadTexto() != null) cuidador.setDisponibilidadTexto(req.getDisponibilidadTexto());
        if (req.getTarifaBase()          != null) cuidador.setTarifaBase(req.getTarifaBase());

        if (req.getFotoUrl() != null && cuidador.getUsuario() != null) {
            cuidador.getUsuario().setFotoUrl(req.getFotoUrl());
            usuarioRepository.save(cuidador.getUsuario());
        }

        cuidador = cuidadorRepository.save(cuidador);
        return modelMapper.map(cuidador, CuidadorDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CuidadorDTO> buscarPorEspecialidad(String especialidad) {
        return cuidadorRepository.findByEspecialidadContainingIgnoreCase(especialidad)
                .stream()
                .map(entidad -> modelMapper.map(entidad, CuidadorDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuidadorDTO> buscarPorUbicacion(String ubicacion) {
        return cuidadorRepository.findByUbicacionContainingIgnoreCase(ubicacion)
                .stream()
                .map(entidad -> modelMapper.map(entidad, CuidadorDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuidadorDTO> buscarPorFiltros(String ubicacion, String especialidad, String disponibilidad) {
        String ub = (ubicacion    == null || ubicacion.isBlank())    ? "" : ubicacion.trim();
        String es = (especialidad == null || especialidad.isBlank()) ? "" : especialidad.trim();
        String di = (disponibilidad == null || disponibilidad.isBlank()) ? "" : disponibilidad.trim();
        return cuidadorRepository.buscarPorFiltros(ub, es, di)
                .stream()
                .map(entidad -> modelMapper.map(entidad, CuidadorDTO.class))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CuidadorDTO> buscarPorCondicionMedica(String condicion) {
        if (condicion == null || condicion.isBlank()) {
            throw new IllegalArgumentException("Debe indicar una condición médica para buscar.");
        }
        return cuidadorRepository.buscarPorCondicionMedica(condicion)
                .stream()
                .map(entidad -> modelMapper.map(entidad, CuidadorDTO.class))
                .toList();
    }
}
