package com.upc.careme.services;

import com.upc.careme.dtos.ProcesarPagoRequestDTO;
import com.upc.careme.entidades.Pago;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.repositorios.PagoRepository;
import com.upc.careme.repositorios.ServicioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void procesar_lanzaExcepcion_siElServicioYaFuePagado() {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(1);
        servicio.setCostoTotal(100.0);

        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicio));
        when(pagoRepository.existsByServicio_IdServicio(1)).thenReturn(true);

        ProcesarPagoRequestDTO request = new ProcesarPagoRequestDTO();
        request.setIdServicio(1);
        request.setMetodoPago("yape");
        request.setDatosTransaccion("token-simulado-123");

        assertThrows(IllegalArgumentException.class, () -> pagoService.procesar(request));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void procesar_tomaElMontoDelServicioGuardadoEnServidor_noDelRequestDelCliente() {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(2);
        servicio.setCostoTotal(250.0);

        when(servicioRepository.findById(2)).thenReturn(Optional.of(servicio));
        when(pagoRepository.existsByServicio_IdServicio(2)).thenReturn(false);

        // ProcesarPagoRequestDTO no tiene ningun campo "monto": el cliente no puede
        // enviar un monto propio, solo el metodo de pago y los datos de la transaccion.
        ProcesarPagoRequestDTO request = new ProcesarPagoRequestDTO();
        request.setIdServicio(2);
        request.setMetodoPago("tarjeta");
        request.setDatosTransaccion("token-abc");

        String resultado = pagoService.procesar(request);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());
        assertEquals(250.0, captor.getValue().getMonto(), 0.001);
        assertEquals("completado", captor.getValue().getEstadoPago());
        assertTrue(resultado.contains("250.0"));
        assertEquals("FINALIZADO", servicio.getEstado());
    }
}
