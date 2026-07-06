package com.upc.careme.services;

import com.upc.careme.dtos.CertificadoRequestDTO;
import com.upc.careme.entidades.CertificadoCuidador;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.repositorios.CertificadoCuidadorRepository;
import com.upc.careme.repositorios.CuidadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoCuidadorRepository certificadoRepository;

    @Autowired
    private CuidadorRepository cuidadorRepository;

    public String subirCertificado(CertificadoRequestDTO request) {

        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("El cuidador especificado no existe."));


        CertificadoCuidador certificado = new CertificadoCuidador();
        certificado.setCuidador(cuidador);
        certificado.setNombre(request.getNombre());
        certificado.setArchivoUrl(request.getArchivoUrl());
        certificado.setFechaSubida(LocalDateTime.now());


        certificadoRepository.save(certificado);

        return "¡Documento '" + request.getNombre() + "' registrado correctamente! Queda pendiente de verificación administrativa.";
    }
}