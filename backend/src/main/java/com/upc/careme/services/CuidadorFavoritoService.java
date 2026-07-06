package com.upc.careme.services;

import com.upc.careme.dtos.CuidadorDTO;
import com.upc.careme.dtos.FavoritoRequestDTO;
import com.upc.careme.entidades.Cuidador;
import com.upc.careme.entidades.CuidadorFavorito;
import com.upc.careme.entidades.Familiar;
import com.upc.careme.repositorios.CuidadorFavoritoRepository;
import com.upc.careme.repositorios.CuidadorRepository;
import com.upc.careme.repositorios.FamiliarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CuidadorFavoritoService {

    @Autowired private CuidadorFavoritoRepository favoritoRepository;
    @Autowired private FamiliarRepository familiarRepository;
    @Autowired private CuidadorRepository cuidadorRepository;
    @Autowired private ModelMapper modelMapper;

    public String agregar(FavoritoRequestDTO request) {
        if (favoritoRepository.existsByFamiliar_IdFamiliarAndCuidador_IdCuidador(
                request.getIdFamiliar(), request.getIdCuidador())) {
            throw new IllegalArgumentException("Este cuidador ya esta en tus favoritos.");
        }
        Familiar familiar = familiarRepository.findById(request.getIdFamiliar())
                .orElseThrow(() -> new IllegalArgumentException("Familiar no encontrado."));
        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado."));

        CuidadorFavorito favorito = new CuidadorFavorito();
        favorito.setFamiliar(familiar);
        favorito.setCuidador(cuidador);
        favorito.setFechaGuardado(LocalDateTime.now());
        favoritoRepository.save(favorito);
        return "Cuidador agregado a favoritos.";
    }

    public String eliminar(Integer idFamiliar, Integer idCuidador) {
        if (!favoritoRepository.existsByFamiliar_IdFamiliarAndCuidador_IdCuidador(idFamiliar, idCuidador)) {
            throw new IllegalArgumentException("Este cuidador no esta en tus favoritos.");
        }
        favoritoRepository.deleteByFamiliar_IdFamiliarAndCuidador_IdCuidador(idFamiliar, idCuidador);
        return "Cuidador eliminado de favoritos.";
    }

    public List<CuidadorDTO> listarPorFamiliar(Integer idFamiliar) {
        return favoritoRepository.findByFamiliarIdFamiliar(idFamiliar)
                .stream()
                .map(f -> modelMapper.map(f.getCuidador(), CuidadorDTO.class))
                .toList();
    }

    public List<Integer> listarIdsPorFamiliar(Integer idFamiliar) {
        return favoritoRepository.findIdsCuidadoresByFamiliar(idFamiliar);
    }
}
