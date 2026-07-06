package com.upc.careme.services;

import com.upc.careme.dtos.MensajeDTO;
import com.upc.careme.dtos.MensajeRequestDTO;
import com.upc.careme.entidades.Mensaje;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.entidades.Usuario;
import com.upc.careme.repositorios.MensajeRepository;
import com.upc.careme.repositorios.ServicioRepository;
import com.upc.careme.repositorios.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;


    public MensajeDTO insertar(MensajeDTO dto) {
        Mensaje entidad = modelMapper.map(dto, Mensaje.class);
        entidad = mensajeRepository.save(entidad);
        return modelMapper.map(entidad, MensajeDTO.class);
    }

    public List<MensajeDTO> listar() {
        return mensajeRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, MensajeDTO.class))
                .toList();
    }


    public String enviarMensaje(MensajeRequestDTO request) {

        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado. No se puede chatear fuera de un servicio."));

        if (!"CONFIRMADO".equals(servicio.getEstado())) {
            throw new IllegalArgumentException("Solo puedes enviar mensajes en servicios activos (CONFIRMADO).");
        }

        Usuario remitente = usuarioRepository.findById(request.getIdRemitente())
                .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado."));


        Mensaje mensaje = new Mensaje();
        mensaje.setServicio(servicio);
        mensaje.setRemitente(remitente);
        mensaje.setContenido(request.getContenido());
        mensaje.setArchivoUrl(request.getArchivoUrl());
        mensaje.setFechaEnvio(LocalDateTime.now());


        mensajeRepository.save(mensaje);

        return "Mensaje enviado a las " + mensaje.getFechaEnvio().toLocalTime();
    }

    public List<MensajeDTO> historialPorServicio(Integer idServicio) {
        Servicio servicio = servicioRepository.findById(idServicio)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado."));

        if ("RECHAZADO".equals(servicio.getEstado()) || "CANCELADO".equals(servicio.getEstado())) {
            throw new IllegalArgumentException("El chat no está disponible para servicios rechazados o cancelados.");
        }

        return mensajeRepository.findByServicioIdServicioOrderByFechaEnvioAsc(idServicio)
                .stream()
                .map(entidad -> modelMapper.map(entidad, MensajeDTO.class))
                .toList();
    }

    @org.springframework.transaction.annotation.Transactional
    public void marcarLeidosServicio(Integer idServicio, Integer idUsuario) {
        mensajeRepository.marcarLeidosByServicioAndNoRemitente(idServicio, idUsuario);
    }

    public Map<Integer, Long> noLeidosPorServicio(Integer idUsuario) {
        List<Object[]> rows = mensajeRepository.countNoLeidosPorServicio(idUsuario);
        Map<Integer, Long> mapa = new HashMap<>();
        for (Object[] row : rows) {
            mapa.put((Integer) row[0], (Long) row[1]);
        }
        return mapa;
    }
}