package com.upc.careme.controllers;

import com.upc.careme.dtos.CertificadoRequestDTO;
import com.upc.careme.services.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @PostMapping("/subir")
    public String subirCertificado(@RequestBody CertificadoRequestDTO request) {
        return certificadoService.subirCertificado(request);
    }
}