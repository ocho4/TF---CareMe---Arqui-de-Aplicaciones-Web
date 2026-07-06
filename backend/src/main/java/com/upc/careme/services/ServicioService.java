package com.upc.careme.services;

import com.upc.careme.dtos.*;
import com.upc.careme.entidades.*;
import com.upc.careme.repositorios.CalificacionRepository;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.PagoRepository;
import com.upc.careme.repositorios.ServicioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private UsuarioDTO toUsuarioDTO(Usuario u) {
        if (u == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setFotoUrl(u.getFotoUrl());
        dto.setTelefono(u.getTelefono());
        dto.setProveedorAuth(u.getProveedorAuth());
        dto.setIdExterno(u.getIdExterno());
        if (u.getTipoUsuario() != null) {
            TipoUsuarioDTO tDto = new TipoUsuarioDTO();
            tDto.setIdTipo(u.getTipoUsuario().getIdTipo());
            tDto.setNombreTipo(u.getTipoUsuario().getNombreTipo());
            tDto.setDescripcion(u.getTipoUsuario().getDescripcion());
            dto.setTipoUsuario(tDto);
        }
        return dto;
    }

    private FamiliarDTO toFamiliarDTO(Familiar f) {
        if (f == null) return null;
        FamiliarDTO dto = new FamiliarDTO();
        dto.setIdFamiliar(f.getIdFamiliar());
        dto.setDireccion(f.getDireccion());
        dto.setDistrito(f.getDistrito());
        dto.setRelacionPaciente(f.getRelacionPaciente());
        dto.setUsuario(toUsuarioDTO(f.getUsuario()));
        return dto;
    }

    private CuidadorDTO toCuidadorDTO(Cuidador c) {
        if (c == null) return null;
        CuidadorDTO dto = new CuidadorDTO();
        dto.setIdCuidador(c.getIdCuidador());
        dto.setEspecialidad(c.getEspecialidad());
        dto.setUbicacion(c.getUbicacion());
        dto.setDisponibilidadTexto(c.getDisponibilidadTexto());
        dto.setTarifaBase(c.getTarifaBase());
        dto.setCalificacionPromedio(c.getCalificacionPromedio());
        dto.setActivo(c.getActivo());
        dto.setObservado(c.getObservado());
        dto.setUsuario(toUsuarioDTO(c.getUsuario()));
        if (c.getCondiciones() != null) {
            dto.setCondiciones(c.getCondiciones().stream().map(cm -> {
                CondicionMedicaDTO cmDto = new CondicionMedicaDTO();
                cmDto.setIdCondicion(cm.getIdCondicion());
                cmDto.setNombreCondicion(cm.getNombreCondicion());
                return cmDto;
            }).toList());
        }
        return dto;
    }

    private PacienteDTO toPacienteDTO(Paciente p) {
        if (p == null) return null;
        PacienteDTO dto = new PacienteDTO();
        dto.setIdPaciente(p.getIdPaciente());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setNecesidadesEspecificas(p.getNecesidadesEspecificas());
        dto.setUsuario(toUsuarioDTO(p.getUsuario()));
        return dto;
    }

    private ServicioDTO toServicioDTO(Servicio s) {
        ServicioDTO dto = new ServicioDTO();
        dto.setIdServicio(s.getIdServicio());
        dto.setFechaInicio(s.getFechaInicio());
        dto.setFechaFin(s.getFechaFin());
        dto.setFechaCancelacion(s.getFechaCancelacion());
        dto.setTipoServicio(s.getTipoServicio());
        dto.setRecargoHorario(s.getRecargoHorario());
        dto.setDescuentoAplicado(s.getDescuentoAplicado());
        dto.setCostoTotal(s.getCostoTotal());
        dto.setEstado(s.getEstado());
        dto.setFamiliar(toFamiliarDTO(s.getFamiliar()));
        dto.setCuidador(toCuidadorDTO(s.getCuidador()));
        dto.setPaciente(toPacienteDTO(s.getPaciente()));
        if (s.getIdServicio() != null) {
            dto.setPagado(pagoRepository.existsByServicio_IdServicio(s.getIdServicio()));
            dto.setCalificado(calificacionRepository.existsByServicio_IdServicio(s.getIdServicio()));
        }
        return dto;
    }

    private ServicioDTO cargarCompleto(Integer id) {
        entityManager.flush();
        entityManager.clear();
        Servicio s = servicioRepository.findByIdCompleto(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        return toServicioDTO(s);
    }

    public ServicioDTO insertar(ServicioDTO dto) {
        if (dto.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes agendar un servicio en el pasado.");
        }
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha y hora de fin debe ser posterior al inicio.");
        }

        Cuidador cuidador = cuidadorRepository.findById(dto.getCuidador().getIdCuidador())
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado"));

        long horas = redondearHoras(Duration.between(dto.getFechaInicio(), dto.getFechaFin()).toMinutes());
        if (horas <= 0) horas = 1;

        double costoCalculado = (cuidador.getTarifaBase() * horas) + dto.getRecargoHorario() - dto.getDescuentoAplicado();
        dto.setCostoTotal(costoCalculado);

        Servicio entidad = modelMapper.map(dto, Servicio.class);
        entidad.setEstado("SOLICITADO");
        entidad = servicioRepository.save(entidad);
        return cargarCompleto(entidad.getIdServicio());
    }

    public List<ServicioDTO> listar() {
        return servicioRepository.findAllCompleto()
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public List<ServicioDTO> listarPorUsuario(Integer idUsuario) {
        return servicioRepository.findByUsuarioCompleto(idUsuario)
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public List<ServicioDTO> listarPorPaciente(Integer idPaciente) {
        return servicioRepository.findByPacienteIdPaciente(idPaciente)
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public List<ServicioDTO> listarPendientesPorCuidadorUsuario(Integer idUsuario) {
        return servicioRepository.findPendientesByCuidadorUsuario(idUsuario)
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public ServicioDTO confirmar(Integer id, Integer idUsuarioCuidador) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        if (!servicio.getCuidador().getUsuario().getIdUsuario().equals(idUsuarioCuidador)) {
            throw new RuntimeException("No tienes permiso para confirmar este servicio.");
        }
        servicio.setEstado("CONFIRMADO");
        servicioRepository.save(servicio);
        return cargarCompleto(id);
    }

    public ServicioDTO rechazar(Integer id, Integer idUsuarioCuidador) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        if (!servicio.getCuidador().getUsuario().getIdUsuario().equals(idUsuarioCuidador)) {
            throw new RuntimeException("No tienes permiso para rechazar este servicio.");
        }
        servicio.setEstado("RECHAZADO");
        servicioRepository.save(servicio);
        return cargarCompleto(id);
    }

    public ServicioDTO cancelar(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        long horasParaInicio = Duration.between(LocalDateTime.now(), servicio.getFechaInicio()).toHours();

        if (horasParaInicio < 24) {
            servicio.setCostoTotal(servicio.getCostoTotal() * 0.20);
        } else {
            servicio.setCostoTotal(0.0);
        }

        servicio.setEstado("CANCELADO");
        servicio.setFechaCancelacion(LocalDateTime.now());
        servicioRepository.save(servicio);
        return cargarCompleto(id);
    }

    public List<ServicioDTO> obtenerProximos(Integer idUsuario) {
        LocalDateTime ahora  = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);
        return servicioRepository.findServiciosProximos(idUsuario, ahora, limite)
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public List<ServicioDTO> obtenerAgenda(Integer idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return servicioRepository.findByAgenda(idUsuario,
                        fechaInicio.atStartOfDay(),
                        fechaFin.atTime(23, 59, 59))
                .stream()
                .map(this::toServicioDTO)
                .toList();
    }

    public CotizacionResponseDTO cotizar(CotizacionRequestDTO request) {
        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new RuntimeException("Cuidador no encontrado."));

        long horas = redondearHoras(Duration.between(request.getFechaInicio(), request.getFechaFin()).toMinutes());
        if (horas <= 0) horas = 1;

        double tarifaBase = cuidador.getTarifaBase();
        double costoBase = tarifaBase * horas;

        double recargoNocturno = Boolean.TRUE.equals(request.getEsHorarioNocturno()) ? costoBase * 0.30 : 0.0;
        double recargoFinDeSemana = Boolean.TRUE.equals(request.getEsFinDeSemana()) ? costoBase * 0.20 : 0.0;
        double recargoEspecialidad = (request.getEspecialidadRequerida() != null && !request.getEspecialidadRequerida().isBlank())
                ? costoBase * 0.15 : 0.0;
        double descuentoLargaDuracion = calcularDescuentoPorDuracion(horas, costoBase);

        double costoTotal = costoBase + recargoNocturno + recargoFinDeSemana + recargoEspecialidad - descuentoLargaDuracion;

        CotizacionResponseDTO response = new CotizacionResponseDTO();
        response.setHoras(horas);
        response.setTarifaBase(tarifaBase);
        response.setCostoBase(costoBase);
        response.setRecargoNocturno(recargoNocturno);
        response.setRecargoFinDeSemana(recargoFinDeSemana);
        response.setRecargoEspecialidad(recargoEspecialidad);
        response.setDescuentoLargaDuracion(descuentoLargaDuracion);
        response.setCostoTotal(costoTotal);
        return response;
    }

    private long redondearHoras(long minutos) {
        long horas = minutos / 60;
        if (minutos % 60 > 15) horas++;
        return horas;
    }

    private double calcularDescuentoPorDuracion(long horas, double costoBase) {
        if (horas > 24) return costoBase * 0.20;
        if (horas > 16) return costoBase * 0.15;
        if (horas > 8)  return costoBase * 0.10;
        return 0.0;
    }

    public ServicioDTO obtenerPorId(Integer id) {
        return cargarCompleto(id);
    }

    public ServicioDTO finalizar(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        if (!"CONFIRMADO".equals(servicio.getEstado())) {
            throw new IllegalArgumentException("Solo puedes finalizar servicios en estado CONFIRMADO.");
        }
        if (servicio.getFechaFin() != null && LocalDateTime.now().isBefore(servicio.getFechaFin())) {
            throw new IllegalArgumentException("No puedes finalizar un servicio antes de su hora de fin.");
        }
        servicio.setEstado("FINALIZADO");
        servicioRepository.save(servicio);
        return cargarCompleto(id);
    }

    public List<ComparativaCostoDTO> compararCostos(ComparativaRequestDTO request) {
        if (request.getIdsCuidadores() == null || request.getIdsCuidadores().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un cuidador para comparar.");
        }

        return request.getIdsCuidadores().stream().map(idCuidador -> {
            Cuidador cuidador = cuidadorRepository.findById(idCuidador)
                    .orElseThrow(() -> new RuntimeException("Cuidador no encontrado: " + idCuidador));

            long horas = Duration.between(request.getFechaInicio(), request.getFechaFin()).toHours();
            if (horas <= 0) horas = 1;

            double tarifaBase = cuidador.getTarifaBase();
            double costoBase = tarifaBase * horas;
            double recargoNocturno = Boolean.TRUE.equals(request.getEsHorarioNocturno()) ? costoBase * 0.30 : 0.0;
            double recargoFinDeSemana = Boolean.TRUE.equals(request.getEsFinDeSemana()) ? costoBase * 0.20 : 0.0;
            double recargoEspecialidad = (request.getEspecialidadRequerida() != null && !request.getEspecialidadRequerida().isBlank())
                    ? costoBase * 0.15 : 0.0;
            double descuentoLargaDuracion = calcularDescuentoPorDuracion(horas, costoBase);
            double costoTotal = costoBase + recargoNocturno + recargoFinDeSemana + recargoEspecialidad - descuentoLargaDuracion;

            ComparativaCostoDTO item = new ComparativaCostoDTO();
            item.setCuidador(toCuidadorDTO(cuidador));
            item.setHoras(horas);
            item.setTarifaBase(tarifaBase);
            item.setCostoBase(costoBase);
            item.setRecargoNocturno(recargoNocturno);
            item.setRecargoFinDeSemana(recargoFinDeSemana);
            item.setRecargoEspecialidad(recargoEspecialidad);
            item.setDescuentoLargaDuracion(descuentoLargaDuracion);
            item.setCostoTotal(costoTotal);
            return item;
        }).toList();
    }
}
