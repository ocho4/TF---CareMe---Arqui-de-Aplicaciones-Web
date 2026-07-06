package com.upc.careme.services;

import com.upc.careme.dtos.CotizacionRequestDTO;
import com.upc.careme.dtos.CotizacionResponseDTO;
import com.upc.careme.dtos.ServicioDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.entidades.Usuario;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.ServicioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @InjectMocks
    private ServicioService servicioService;

    @Test
    void cotizar_calculaCostoTotalConRecargosYDescuentoAplicados() {
        Cuidador cuidador = new Cuidador();
        cuidador.setTarifaBase(20.0);
        when(cuidadorRepository.findById(5)).thenReturn(Optional.of(cuidador));

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setIdCuidador(5);
        request.setFechaInicio(LocalDateTime.now());
        request.setFechaFin(LocalDateTime.now().plusHours(5));
        request.setEsHorarioNocturno(true);
        request.setEsFinDeSemana(true);
        request.setEspecialidadRequerida("Alzheimer");

        CotizacionResponseDTO response = servicioService.cotizar(request);

        // costoBase = 20 * 5h = 100; +30% nocturno (30) +20% fin de semana (20)
        // +15% especialidad (15) - 0% descuento (5h no supera el umbral de 8h)
        assertEquals(100.0, response.getCostoBase(), 0.001);
        assertEquals(30.0, response.getRecargoNocturno(), 0.001);
        assertEquals(20.0, response.getRecargoFinDeSemana(), 0.001);
        assertEquals(15.0, response.getRecargoEspecialidad(), 0.001);
        assertEquals(0.0, response.getDescuentoLargaDuracion(), 0.001);
        assertEquals(165.0, response.getCostoTotal(), 0.001);
    }

    @Test
    void insertar_lanzaExcepcion_siFechaInicioEstaEnElPasado() {
        ServicioDTO dto = new ServicioDTO();
        dto.setFechaInicio(LocalDateTime.now().minusDays(1));
        dto.setFechaFin(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> servicioService.insertar(dto));
    }

    @Test
    void insertar_lanzaExcepcion_siFechaFinEsAnteriorAFechaInicio() {
        ServicioDTO dto = new ServicioDTO();
        dto.setFechaInicio(LocalDateTime.now().plusDays(2));
        dto.setFechaFin(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> servicioService.insertar(dto));
    }

    @Test
    void confirmar_lanzaExcepcion_siElUsuarioNoEsElCuidadorAsignado() {
        Usuario usuarioCuidadorReal = new Usuario();
        usuarioCuidadorReal.setIdUsuario(1);

        Cuidador cuidadorAsignado = new Cuidador();
        cuidadorAsignado.setUsuario(usuarioCuidadorReal);

        Servicio servicio = new Servicio();
        servicio.setIdServicio(10);
        servicio.setCuidador(cuidadorAsignado);

        when(servicioRepository.findById(10)).thenReturn(Optional.of(servicio));

        // Otro cuidador (idUsuario 999) intenta confirmar un servicio que no es suyo.
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> servicioService.confirmar(10, 999));
        assertEquals("No tienes permiso para confirmar este servicio.", ex.getMessage());
    }
}
