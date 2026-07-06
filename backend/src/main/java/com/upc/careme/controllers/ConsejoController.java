package com.upc.careme.controllers;

import com.upc.careme.dtos.ConsejoRequestDTO;
import com.upc.careme.services.ConsejoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consejos")
public class ConsejoController {

    @Autowired
    private ConsejoService consejoService;

    @PostMapping("/publicar")
    public String publicarConsejo(@RequestBody ConsejoRequestDTO request) {
        return consejoService.publicarConsejo(request);
    }
}