package com.upc.careme.services;

import com.upc.careme.dtos.NotificacionDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.Familiar;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.FamiliarRepository;
import com.upc.careme.repositorios.MensajeRepository;
import com.upc.careme.repositorios.ServicioRepository;
import com.upc.careme.repositorios.TareaServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired private CuidadorRepository cuidadorRepository;
    @Autowired private FamiliarRepository familiarRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private TareaServicioRepository tareaRepository;

    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerParaCuidador(Integer idUsuario) {
        List<NotificacionDTO> notificaciones = new ArrayList<>();

        Cuidador cuidador = cuidadorRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado."));

        Integer idCuidador = cuidador.getIdCuidador();

        // 1. Perfil incompleto
        boolean perfilIncompleto = cuidador.getEspecialidad() == null
                || cuidador.getEspecialidad().isBlank()
                || cuidador.getUbicacion() == null
                || cuidador.getUbicacion().isBlank()
                || cuidador.getTarifaBase() == null
                || cuidador.getTarifaBase() == 0.0;

        if (perfilIncompleto) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("perfil");
            n.setTitulo("Completa tu perfil");
            n.setDescripcion("Añade tu especialidad, dirección y tarifa para aparecer en las búsquedas de familias.");
            n.setRuta("/perfil");
            n.setCantidad(1);
            notificaciones.add(n);
        }

        // 2. Solicitudes nuevas
        long solicitudes = servicioRepository.countByCuidadorIdCuidadorAndEstado(idCuidador, "SOLICITADO");
        if (solicitudes > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("solicitud");
            n.setTitulo("Solicitud de servicio");
            n.setDescripcion(solicitudes == 1
                    ? "Tienes 1 nueva solicitud pendiente de respuesta."
                    : "Tienes " + solicitudes + " nuevas solicitudes pendientes.");
            n.setRuta("/pendientes");
            n.setCantidad((int) solicitudes);
            notificaciones.add(n);
        }

        // 3. Mensajes no leídos (enviados por el familiar, no por el cuidador)
        Integer idUsuarioCuidador = cuidador.getUsuario().getIdUsuario();
        long mensajes = mensajeRepository.countMensajesNoLeidosByCuidador(idCuidador, idUsuarioCuidador);
        if (mensajes > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("mensaje");
            n.setTitulo("Mensajes sin leer");
            n.setDescripcion(mensajes == 1
                    ? "Tienes 1 mensaje sin leer de un familiar."
                    : "Tienes " + mensajes + " mensajes sin leer.");
            n.setRuta("/mis-servicios");
            n.setCantidad((int) mensajes);
            notificaciones.add(n);
        }

        // 4. Tareas nuevas del familiar sin ver
        long tareasNuevas = tareaRepository.countNuevasSinVerByCuidador(idUsuario);
        if (tareasNuevas > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("tarea");
            n.setTitulo("Nueva instrucción añadida");
            n.setDescripcion(tareasNuevas == 1
                    ? "Un familiar ha añadido 1 nueva instrucción a un servicio."
                    : "Se han añadido " + tareasNuevas + " nuevas instrucciones a tus servicios.");
            n.setRuta("/mis-servicios");
            n.setCantidad((int) tareasNuevas);
            notificaciones.add(n);
        }

        // 5. Servicios próximos (CONFIRMADO en las próximas 24 h)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);
        long proximos = servicioRepository.countServiciosProximosCuidador(idCuidador, ahora, limite);
        if (proximos > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("proximo");
            n.setTitulo("Servicio próximo");
            n.setDescripcion(proximos == 1
                    ? "Tienes 1 servicio que comienza en las próximas 24 horas."
                    : "Tienes " + proximos + " servicios en las próximas 24 horas.");
            n.setRuta("/agenda");
            n.setCantidad((int) proximos);
            notificaciones.add(n);
        }

        return notificaciones;
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerParaFamiliar(Integer idUsuario) {
        List<NotificacionDTO> notificaciones = new ArrayList<>();

        Familiar familiar = familiarRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Familiar no encontrado."));

        // 1. Mensajes sin leer (enviados por el cuidador, no por el familiar)
        long mensajes = mensajeRepository.countMensajesNoLeidosByFamiliar(idUsuario);
        if (mensajes > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("mensaje");
            n.setTitulo("Mensajes sin leer");
            n.setDescripcion(mensajes == 1
                    ? "Tienes 1 mensaje sin leer de un cuidador."
                    : "Tienes " + mensajes + " mensajes sin leer.");
            n.setRuta("/mis-servicios");
            n.setCantidad((int) mensajes);
            notificaciones.add(n);
        }

        // 2. Servicios con pago pendiente (CONFIRMADO + fechaFin ya pasó)
        LocalDateTime ahora = LocalDateTime.now();
        long pagosPendientes = servicioRepository.countServiciosConPagoPendienteFamiliar(idUsuario, ahora);
        if (pagosPendientes > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("pago");
            n.setTitulo("Pago pendiente");
            n.setDescripcion(pagosPendientes == 1
                    ? "Un servicio ha terminado con éxito. Realiza el pago para finalizar."
                    : pagosPendientes + " servicios han terminado. Tienes pagos pendientes.");
            n.setRuta("/mis-servicios");
            n.setCantidad((int) pagosPendientes);
            notificaciones.add(n);
        }

        // 3. Tareas marcadas como completadas por el cuidador sin ver
        long tareasCompletadas = tareaRepository.countCompletadasSinVerByFamiliar(idUsuario);
        if (tareasCompletadas > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("tarea");
            n.setTitulo("Tarea completada");
            n.setDescripcion(tareasCompletadas == 1
                    ? "La cuidadora ha marcado 1 tarea como completada."
                    : "La cuidadora ha marcado " + tareasCompletadas + " tareas como completadas.");
            n.setRuta("/mis-servicios");
            n.setCantidad((int) tareasCompletadas);
            notificaciones.add(n);
        }

        // 4. Servicios próximos (CONFIRMADO en las próximas 24 h)
        LocalDateTime limite = ahora.plusHours(24);
        long proximos = servicioRepository.countServiciosProximosFamiliar(idUsuario, ahora, limite);
        if (proximos > 0) {
            NotificacionDTO n = new NotificacionDTO();
            n.setTipo("proximo");
            n.setTitulo("Servicio próximo");
            n.setDescripcion(proximos == 1
                    ? "Tienes 1 servicio que comienza en las próximas 24 horas."
                    : "Tienes " + proximos + " servicios en las próximas 24 horas.");
            n.setRuta("/agenda");
            n.setCantidad((int) proximos);
            notificaciones.add(n);
        }

        return notificaciones;
    }
}
