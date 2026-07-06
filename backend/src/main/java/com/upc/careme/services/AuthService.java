package com.upc.careme.services;

import com.upc.careme.config.CuidadorInactivoException;
import com.upc.careme.dtos.LoginRequestDTO;
import com.upc.careme.dtos.RegistroUsuarioDTO;
import com.upc.careme.dtos.RepostularDTO;
import com.upc.careme.dtos.UsuarioDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.Familiar;
import com.upc.careme.entidades.TipoUsuario;
import com.upc.careme.entidades.Usuario;
import com.upc.careme.entidades.VerificacionCuidador;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.FamiliarRepository;
import com.upc.careme.repositorios.UsuarioRepository;
import com.upc.careme.repositorios.VerificacionCuidadorRepository;
import com.upc.careme.security.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Autowired
    private VerificacionCuidadorRepository verificacionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UsuarioDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        if ("cuidador".equals(usuario.getRol())) {
            Cuidador cuidador = cuidadorRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil de cuidador no encontrado."));

            if (!Boolean.TRUE.equals(cuidador.getActivo())) {
                VerificacionCuidador v = verificacionRepository
                        .findTopByCuidadorIdCuidadorOrderByFechaSolicitudDesc(cuidador.getIdCuidador())
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud de verificación."));

                if ("pendiente".equals(v.getEstado())) {
                    throw new CuidadorInactivoException(
                            "pendiente",
                            "Tu solicitud está siendo revisada por el equipo CareMe. Te notificaremos cuando sea aprobada.",
                            null, null);
                } else if ("rechazado".equals(v.getEstado())) {
                    throw new CuidadorInactivoException(
                            "rechazado",
                            "Tu solicitud fue rechazada. Puedes enviar una nueva solicitud con más información.",
                            v.getObservaciones(),
                            usuario.getIdUsuario());
                }
            }
        }

        UsuarioDTO dto = modelMapper.map(usuario, UsuarioDTO.class);
        dto.setToken(jwtUtil.generateToken(usuario.getEmail(), usuario.getRol()));
        return dto;
    }

    @Transactional
    public void repostular(RepostularDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña incorrectos."));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
        }

        if (!"cuidador".equals(usuario.getRol())) {
            throw new IllegalArgumentException("Esta cuenta no es de cuidador.");
        }

        Cuidador cuidador = cuidadorRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Perfil de cuidador no encontrado."));

        VerificacionCuidador v = verificacionRepository
                .findTopByCuidadorIdCuidadorOrderByFechaSolicitudDesc(cuidador.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud previa."));

        if (!"rechazado".equals(v.getEstado())) {
            throw new IllegalArgumentException("Solo puedes repostular si tu solicitud fue rechazada.");
        }

        VerificacionCuidador nueva = new VerificacionCuidador();
        nueva.setCuidador(cuidador);
        nueva.setEstado("pendiente");
        nueva.setMotivacion(dto.getMotivacion());
        verificacionRepository.save(nueva);
    }

    @Transactional
    public UsuarioDTO registrar(RegistroUsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Este correo ya está registrado en Care Me.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setNombres(dto.getNombres());
        nuevoUsuario.setApellidos(dto.getApellidos());
        nuevoUsuario.setTelefono(dto.getTelefono());

        TipoUsuario tipo = new TipoUsuario();
        tipo.setIdTipo(dto.getIdTipo());
        nuevoUsuario.setTipoUsuario(tipo);

        nuevoUsuario.setRol(dto.getIdTipo() == 1 ? "familiar" : "cuidador");

        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        if (dto.getIdTipo() == 1) {
            Familiar familiar = new Familiar();
            familiar.setUsuario(nuevoUsuario);
            familiarRepository.save(familiar);
        } else if (dto.getIdTipo() == 2) {
            Cuidador cuidador = new Cuidador();
            cuidador.setUsuario(nuevoUsuario);
            cuidador.setUbicacion("");
            cuidador.setTarifaBase(0.0);
            cuidador.setActivo(false);
            cuidador.setObservado(false);
            cuidador = cuidadorRepository.save(cuidador);

            VerificacionCuidador verificacion = new VerificacionCuidador();
            verificacion.setCuidador(cuidador);
            verificacion.setEstado("pendiente");
            verificacion.setMotivacion(dto.getMotivacion());
            verificacionRepository.save(verificacion);
        }

        UsuarioDTO resultado = modelMapper.map(nuevoUsuario, UsuarioDTO.class);
        resultado.setToken(jwtUtil.generateToken(nuevoUsuario.getEmail(), nuevoUsuario.getRol()));
        return resultado;
    }

    public String recuperarPassword(String email, String telefono) {
        Usuario usuario;

        if (email != null && !email.isBlank()) {
            usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta registrada con ese correo."));
        } else if (telefono != null && !telefono.isBlank()) {
            usuario = usuarioRepository.findByTelefono(telefono)
                    .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta asociada a ese número de teléfono."));
        } else {
            throw new IllegalArgumentException("Debe proporcionar un correo electrónico o número de teléfono.");
        }

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setTokenExpiracion(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        String destino = (email != null && !email.isBlank())
                ? "correo: " + email
                : "teléfono: " + telefono;

        return "Enlace de recuperación generado. Token: " + token +
                ". Válido por 1 hora. (Simulado — en producción se enviaría al " + destino + ")";
    }

    public String resetPassword(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o ya utilizado."));

        if (LocalDateTime.now().isAfter(usuario.getTokenExpiracion())) {
            throw new IllegalArgumentException("El token ha expirado. Solicita un nuevo enlace de recuperación.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setTokenExpiracion(null);
        usuarioRepository.save(usuario);

        return "Contraseña actualizada correctamente. Ya puedes iniciar sesión con tu nueva contraseña.";
    }
}
