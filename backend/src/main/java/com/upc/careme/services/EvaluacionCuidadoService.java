package com.upc.careme.services;

import com.upc.careme.dtos.EvaluacionCuidadoDTO;
import com.upc.careme.entidades.EvaluacionCuidado;
import com.upc.careme.repositorios.EvaluacionCuidadoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluacionCuidadoService {

    @Autowired
    private EvaluacionCuidadoRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public EvaluacionCuidadoDTO insertar(EvaluacionCuidadoDTO dto) {
        EvaluacionCuidado entidad = modelMapper.map(dto, EvaluacionCuidado.class);
        entidad = repository.save(entidad);
        return modelMapper.map(entidad, EvaluacionCuidadoDTO.class);
    }

    public List<EvaluacionCuidadoDTO> listar() {
        return repository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, EvaluacionCuidadoDTO.class))
                .toList();
    }
}