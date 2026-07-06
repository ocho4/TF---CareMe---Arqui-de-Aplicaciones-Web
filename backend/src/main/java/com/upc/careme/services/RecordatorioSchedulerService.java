package com.upc.careme.services;

import com.upc.careme.entidades.Servicio;
import com.upc.careme.repositorios.ServicioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecordatorioSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioSchedulerService.class);

    @Autowired
    private ServicioRepository servicioRepository;

    @Scheduled(fixedRate = 3600000)
    public void alertarServiciosProximos() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(24);

        List<Servicio> servicios = servicioRepository.findServiciosParaAlerta(ahora, limite);

        for (Servicio s : servicios) {
            long horasRestantes = Duration.between(ahora, s.getFechaInicio()).toHours();
            log.info("[ALERTA] Servicio #{} inicia en {} hora(s). Familiar: {} {} | Cuidador: {} {}",
                    s.getIdServicio(),
                    horasRestantes,
                    s.getFamiliar().getUsuario().getNombres(),
                    s.getFamiliar().getUsuario().getApellidos(),
                    s.getCuidador().getUsuario().getNombres(),
                    s.getCuidador().getUsuario().getApellidos());
        }

        if (servicios.isEmpty()) {
            log.info("[SCHEDULER] No hay servicios próximos en las siguientes 24 horas.");
        } else {
            log.info("[SCHEDULER] {} servicio(s) próximo(s) procesado(s). Alertas despachadas.", servicios.size());
        }
    }
}
