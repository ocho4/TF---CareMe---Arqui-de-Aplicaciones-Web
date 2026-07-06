package com.upc.careme.controllers;

import com.upc.careme.dtos.CuidadorDTO;
import com.upc.careme.dtos.FavoritoRequestDTO;
import com.upc.careme.services.CuidadorFavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class CuidadorFavoritoController {

    @Autowired
    private CuidadorFavoritoService favoritoService;

    @PostMapping
    public String agregar(@RequestBody FavoritoRequestDTO request) {
        return favoritoService.agregar(request);
    }

    @DeleteMapping
    public String eliminar(@RequestParam Integer idFamiliar,
                           @RequestParam Integer idCuidador) {
        return favoritoService.eliminar(idFamiliar, idCuidador);
    }

    @GetMapping("/familiar/{idFamiliar}")
    public List<CuidadorDTO> listarPorFamiliar(@PathVariable Integer idFamiliar) {
        return favoritoService.listarPorFamiliar(idFamiliar);
    }

    @GetMapping("/familiar/{idFamiliar}/ids")
    public List<Integer> listarIdsPorFamiliar(@PathVariable Integer idFamiliar) {
        return favoritoService.listarIdsPorFamiliar(idFamiliar);
    }
}
