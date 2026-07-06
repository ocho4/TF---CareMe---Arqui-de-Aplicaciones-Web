package com.upc.careme.services;

import com.upc.careme.dtos.CalificacionDTO;
import com.upc.careme.dtos.CalificacionFamiliarDTO;
import com.upc.careme.dtos.CalificacionFamiliarRequestDTO;
import com.upc.careme.dtos.CalificacionRequestDTO;
import com.upc.careme.entidades.Calificacion;
import com.upc.careme.entidades.CalificacionFamiliar;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.Familiar;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.repositorios.CalificacionFamiliarRepository;
import com.upc.careme.repositorios.CalificacionRepository;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.FamiliarRepository;
import com.upc.careme.repositorios.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CalificacionService {

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private CalificacionFamiliarRepository calificacionFamiliarRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private FamiliarRepository familiarRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CalificacionDTO insertar(CalificacionDTO dto) {
        Calificacion entidad = modelMapper.map(dto, Calificacion.class);
        entidad = calificacionRepository.save(entidad);
        return modelMapper.map(entidad, CalificacionDTO.class);
    }

    public List<CalificacionDTO> listar() {
        return calificacionRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, CalificacionDTO.class))
                .toList();
    }


    public String registrarResena(CalificacionRequestDTO request) {

        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("El servicio especificado no existe."));

        if (!"FINALIZADO".equals(servicio.getEstado())) {
            throw new IllegalArgumentException("Solo puedes calificar servicios que ya han sido finalizados.");
        }

        if (calificacionRepository.existsByServicio_IdServicio(request.getIdServicio())) {
            throw new IllegalArgumentException("Este servicio ya ha sido calificado.");
        }


        Familiar familiar = familiarRepository.findById(request.getIdFamiliar())
                .orElseThrow(() -> new IllegalArgumentException("Familiar no encontrado."));

        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado."));


        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5 estrellas.");
        }


        Calificacion calificacion = new Calificacion();
        calificacion.setServicio(servicio);
        calificacion.setFamiliar(familiar);
        calificacion.setCuidador(cuidador);
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());
        calificacion.setFechaRegistro(LocalDateTime.now());

        calificacionRepository.saveAndFlush(calificacion);

        // Recalcular y persistir el promedio del cuidador
        List<Calificacion> todasLasResenas = calificacionRepository.findByCuidador_IdCuidador(cuidador.getIdCuidador());
        double promedio = todasLasResenas.stream()
                .mapToInt(Calificacion::getPuntuacion)
                .average()
                .orElse(0.0);
        cuidador.setCalificacionPromedio(Math.round(promedio * 100.0) / 100.0);
        cuidadorRepository.saveAndFlush(cuidador);

        return "¡Gracias por tu reseña! Le has dado " + request.getPuntuacion() + " estrellas al cuidador y ayudado a la comunidad.";
    }

    public String registrarResenaFamiliar(CalificacionFamiliarRequestDTO request) {
        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("El servicio especificado no existe."));
        if (!"FINALIZADO".equals(servicio.getEstado())) {
            throw new IllegalArgumentException("Solo puedes calificar servicios que ya han sido finalizados.");
        }
        if (calificacionFamiliarRepository.existsByServicio_IdServicio(request.getIdServicio())) {
            throw new IllegalArgumentException("Este servicio ya ha sido calificado por el cuidador.");
        }
        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuacion debe estar entre 1 y 5 estrellas.");
        }
        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado."));
        Familiar familiar = familiarRepository.findById(request.getIdFamiliar())
                .orElseThrow(() -> new IllegalArgumentException("Familiar no encontrado."));

        CalificacionFamiliar cal = new CalificacionFamiliar();
        cal.setServicio(servicio);
        cal.setCuidador(cuidador);
        cal.setFamiliar(familiar);
        cal.setPuntuacion(request.getPuntuacion());
        cal.setComentario(request.getComentario());
        cal.setFechaRegistro(LocalDateTime.now());
        calificacionFamiliarRepository.save(cal);

        return "Calificacion al familiar registrada correctamente.";
    }

    public List<CalificacionFamiliarDTO> listarPorFamiliar(Integer idFamiliar) {
        return calificacionFamiliarRepository.findByFamiliarConCuidador(idFamiliar)
                .stream()
                .map(c -> {
                    CalificacionFamiliarDTO dto = new CalificacionFamiliarDTO();
                    dto.setIdCalificacionFamiliar(c.getIdCalificacionFamiliar());
                    dto.setPuntuacion(c.getPuntuacion());
                    dto.setComentario(c.getComentario());
                    dto.setFechaRegistro(c.getFechaRegistro());
                    if (c.getCuidador() != null && c.getCuidador().getUsuario() != null) {
                        com.upc.careme.dtos.CuidadorDTO cDto = new com.upc.careme.dtos.CuidadorDTO();
                        com.upc.careme.dtos.UsuarioDTO uDto = new com.upc.careme.dtos.UsuarioDTO();
                        uDto.setNombres(c.getCuidador().getUsuario().getNombres());
                        uDto.setApellidos(c.getCuidador().getUsuario().getApellidos());
                        uDto.setFotoUrl(c.getCuidador().getUsuario().getFotoUrl());
                        cDto.setUsuario(uDto);
                        dto.setCuidador(cDto);
                    }
                    return dto;
                })
                .toList();
    }

    public List<Integer> listarIdsServiciosCalificadosPorCuidador(Integer idCuidador) {
        return calificacionFamiliarRepository.findIdServiciosByCuidador(idCuidador);
    }

    public List<CalificacionDTO> listarPorCuidador(Integer idCuidador) {
        return calificacionRepository.findByCuidadorConFamiliar(idCuidador)
                .stream()
                .map(c -> {
                    CalificacionDTO dto = new CalificacionDTO();
                    dto.setIdCalificacion(c.getIdCalificacion());
                    dto.setPuntuacion(c.getPuntuacion());
                    dto.setComentario(c.getComentario());
                    dto.setFechaRegistro(c.getFechaRegistro());
                    if (c.getFamiliar() != null && c.getFamiliar().getUsuario() != null) {
                        com.upc.careme.dtos.FamiliarDTO fDto = new com.upc.careme.dtos.FamiliarDTO();
                        com.upc.careme.dtos.UsuarioDTO uDto = new com.upc.careme.dtos.UsuarioDTO();
                        uDto.setNombres(c.getFamiliar().getUsuario().getNombres());
                        uDto.setApellidos(c.getFamiliar().getUsuario().getApellidos());
                        fDto.setUsuario(uDto);
                        dto.setFamiliar(fDto);
                    }
                    return dto;
                })
                .toList();
    }
}