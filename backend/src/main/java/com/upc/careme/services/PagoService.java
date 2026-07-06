package com.upc.careme.services;

import com.upc.careme.dtos.PagoDTO;
import com.upc.careme.dtos.PagoRequestDTO;
import com.upc.careme.dtos.ProcesarPagoRequestDTO;
import com.upc.careme.entidades.Pago;
import com.upc.careme.entidades.Servicio;
import com.upc.careme.repositorios.PagoRepository;
import com.upc.careme.repositorios.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ModelMapper modelMapper;


    public PagoDTO insertar(PagoDTO dto) {
        Pago entidad = modelMapper.map(dto, Pago.class);
        entidad = pagoRepository.save(entidad);
        return modelMapper.map(entidad, PagoDTO.class);
    }

    public List<PagoDTO> listar() {
        return pagoRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, PagoDTO.class))
                .toList();
    }

    public List<PagoDTO> listarPorUsuario(Integer idUsuario) {
        return pagoRepository.findByUsuario(idUsuario)
                .stream()
                .map(entidad -> modelMapper.map(entidad, PagoDTO.class))
                .toList();
    }


    public String registrarPagoYape(PagoRequestDTO request) {

        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("El servicio especificado no existe."));


        if (pagoRepository.existsByServicio_IdServicio(servicio.getIdServicio())) {
            throw new IllegalArgumentException("Este servicio ya se encuentra pagado.");
        }


        Pago pago = new Pago();
        pago.setServicio(servicio);
        pago.setMonto(request.getMonto());
        pago.setTelefonoYape(request.getTelefonoYape());
        pago.setCodigoOperacion(request.getCodigoOperacion());
        pago.setMetodoPago("yape");
        pago.setEstadoPago("completado");
        pago.setFechaPago(LocalDateTime.now());


        pagoRepository.save(pago);


        servicio.setEstado("FINALIZADO");
        servicioRepository.save(servicio);

        return "¡Éxito! Pago por S/" + request.getMonto() + " validado. El servicio #" + servicio.getIdServicio() + " ha sido finalizado.";
    }

    public String procesar(ProcesarPagoRequestDTO request) {
        if (request.getDatosTransaccion() == null || request.getDatosTransaccion().isBlank()) {
            throw new IllegalArgumentException("Los datos de transacción encriptados son requeridos.");
        }

        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado."));

        if (pagoRepository.existsByServicio_IdServicio(servicio.getIdServicio())) {
            throw new IllegalArgumentException("Este servicio ya ha sido pagado.");
        }

        Pago pago = new Pago();
        pago.setServicio(servicio);
        pago.setMonto(servicio.getCostoTotal());
        pago.setEstadoPago("completado");
        pago.setFechaPago(LocalDateTime.now());
        pago.setCodigoOperacion("TXN-" + System.currentTimeMillis());
        pago.setMetodoPago(request.getMetodoPago());
        pago.setObservacion("Método: " + request.getMetodoPago() + " | Transacción: " + request.getDatosTransaccion());

        if ("yape".equalsIgnoreCase(request.getMetodoPago()) && request.getTelefonoYape() != null) {
            pago.setTelefonoYape(request.getTelefonoYape());
        } else {
            pago.setTelefonoYape("N/A");
        }

        pagoRepository.save(pago);

        servicio.setEstado("FINALIZADO");
        servicioRepository.save(servicio);

        return "Pago procesado exitosamente. Método: " + request.getMetodoPago() +
                ". Monto: S/" + servicio.getCostoTotal() +
                ". Código de operación: " + pago.getCodigoOperacion() +
                ". Comprobante generado para el servicio #" + servicio.getIdServicio() + ".";
    }
}