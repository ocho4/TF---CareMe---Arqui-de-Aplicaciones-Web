package com.upc.careme.services;

import com.upc.careme.dtos.CondicionMedicaDTO;
import com.upc.careme.entidades.CondicionMedica;
import com.upc.careme.repositorios.CondicionMedicaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CondicionMedicaService {

    @Autowired
    private CondicionMedicaRepository condicionMedicaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CondicionMedicaDTO insertar(CondicionMedicaDTO dto) {
        CondicionMedica entidad = modelMapper.map(dto, CondicionMedica.class);
        entidad = condicionMedicaRepository.save(entidad);
        return modelMapper.map(entidad, CondicionMedicaDTO.class);
    }

    public List<CondicionMedicaDTO> listar() {
        return condicionMedicaRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, CondicionMedicaDTO.class))
                .toList();
    }
}