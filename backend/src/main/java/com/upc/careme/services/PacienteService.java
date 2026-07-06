package com.upc.careme.services;

import com.upc.careme.dtos.PacienteDTO;
import com.upc.careme.dtos.PerfilPacienteRequestDTO;
import com.upc.careme.dtos.RegistrarPacienteFamiliarDTO;
import com.upc.careme.entidades.*;
import com.upc.careme.repositorios.FamiliarPacienteRepository;
import com.upc.careme.repositorios.FamiliarRepository;
import com.upc.careme.repositorios.PacienteRepository;
import com.upc.careme.repositorios.ServicioRepository;
import com.upc.careme.repositorios.TipoUsuarioRepository;
import com.upc.careme.repositorios.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private FamiliarPacienteRepository familiarPacienteRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PacienteDTO insertar(PacienteDTO dto) {
        Paciente entidad = modelMapper.map(dto, Paciente.class);
        entidad = pacienteRepository.save(entidad);
        return modelMapper.map(entidad, PacienteDTO.class);
    }

    public List<PacienteDTO> listar() {
        return pacienteRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, PacienteDTO.class))
                .toList();
    }

    public PacienteDTO buscarPorUsuario(Integer idUsuario) {
        Paciente p = pacienteRepository.findByUsuario_IdUsuario(idUsuario).orElse(null);
        if (p == null) return null;
        return modelMapper.map(p, PacienteDTO.class);
    }

    @Transactional
    public PacienteDTO actualizarPerfil(Integer idPaciente, PerfilPacienteRequestDTO req) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (req.getNecesidadesEspecificas() != null)
            paciente.setNecesidadesEspecificas(req.getNecesidadesEspecificas());
        if (req.getFechaNacimiento() != null)
            paciente.setFechaNacimiento(req.getFechaNacimiento());

        Usuario usuario = paciente.getUsuario();
        if (usuario != null) {
            if (req.getFotoUrl()   != null) usuario.setFotoUrl(req.getFotoUrl());
            if (req.getNombres()   != null) usuario.setNombres(req.getNombres());
            if (req.getApellidos() != null) usuario.setApellidos(req.getApellidos());
            if (req.getTelefono()  != null) usuario.setTelefono(req.getTelefono());
            usuarioRepository.save(usuario);
        }

        paciente = pacienteRepository.save(paciente);

        if (req.getParentesco() != null) {
            familiarPacienteRepository.findByPacienteIdPaciente(idPaciente).ifPresent(fp -> {
                fp.setParentesco(req.getParentesco());
                familiarPacienteRepository.save(fp);
            });
        }

        PacienteDTO dto = modelMapper.map(paciente, PacienteDTO.class);
        familiarPacienteRepository.findByPacienteIdPaciente(idPaciente)
                .ifPresent(fp -> dto.setParentesco(fp.getParentesco()));
        return dto;
    }

    @Transactional
    public void eliminar(Integer idPaciente) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Familiar familiar = familiarRepository.findByUsuario_IdUsuario(usuarioAutenticado.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Solo un familiar puede eliminar un paciente."));

        FamiliarPaciente vinculo = familiarPacienteRepository.findByPacienteIdPaciente(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        if (!vinculo.getFamiliar().getIdFamiliar().equals(familiar.getIdFamiliar())) {
            throw new RuntimeException("No tienes permiso para eliminar este paciente.");
        }

        if (!servicioRepository.findByPacienteIdPaciente(idPaciente).isEmpty()) {
            throw new IllegalArgumentException(
                "No puedes eliminar este paciente porque ya tiene servicios registrados en su historial.");
        }

        familiarPacienteRepository.delete(vinculo);
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Integer idUsuario = paciente.getUsuario() != null ? paciente.getUsuario().getIdUsuario() : null;
        pacienteRepository.delete(paciente);
        if (idUsuario != null) usuarioRepository.deleteById(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<PacienteDTO> listarPorFamiliar(Integer idFamiliar) {
        return familiarPacienteRepository.findByFamiliarIdFamiliar(idFamiliar)
                .stream()
                .map(fp -> {
                    PacienteDTO dto = modelMapper.map(fp.getPaciente(), PacienteDTO.class);
                    dto.setParentesco(fp.getParentesco());
                    return dto;
                })
                .toList();
    }

    @Transactional
    public PacienteDTO registrarParaFamiliar(Integer idFamiliar, RegistrarPacienteFamiliarDTO req) {
        Familiar familiar = familiarRepository.findById(idFamiliar)
                .orElseThrow(() -> new RuntimeException("Familiar no encontrado"));

        TipoUsuario tipoPaciente = tipoUsuarioRepository.findByNombreTipo("paciente")
                .orElseThrow(() -> new RuntimeException("Tipo 'paciente' no encontrado en BD"));


        String email = (req.getEmail() != null && !req.getEmail().isBlank())
                ? req.getEmail()
                : "paciente_" + UUID.randomUUID() + "@careme.internal";

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(tipoPaciente);
        usuario.setEmail(email);
        usuario.setPasswordHash(null);
        usuario.setProveedorAuth("local");
        usuario.setRol("paciente");
        usuario.setNombres(req.getNombres());
        usuario.setApellidos(req.getApellidos());
        usuario.setTelefono(req.getTelefono());
        usuario.setFotoUrl(req.getFotoUrl());
        usuario = usuarioRepository.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setFechaNacimiento(req.getFechaNacimiento());
        paciente.setNecesidadesEspecificas(req.getNecesidadesEspecificas());
        paciente = pacienteRepository.save(paciente);

        FamiliarPaciente fp = new FamiliarPaciente();
        fp.setFamiliar(familiar);
        fp.setPaciente(paciente);
        fp.setParentesco(req.getParentesco());
        fp.setEsPrincipal(true);
        familiarPacienteRepository.save(fp);

        PacienteDTO dto = modelMapper.map(paciente, PacienteDTO.class);
        dto.setParentesco(req.getParentesco());
        return dto;
    }
}
