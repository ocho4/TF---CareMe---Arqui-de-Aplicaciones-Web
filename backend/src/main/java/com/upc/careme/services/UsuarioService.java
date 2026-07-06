package com.upc.careme.services;

import com.upc.careme.dtos.UsuarioDTO;
import com.upc.careme.entidades.Usuario;
import com.upc.careme.repositorios.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UsuarioDTO insertar(UsuarioDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya se encuentra registrado.");
        }

        Usuario entidad = modelMapper.map(dto, Usuario.class);
        entidad = usuarioRepository.save(entidad);
        return modelMapper.map(entidad, UsuarioDTO.class);
    }

    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(entidad -> modelMapper.map(entidad, UsuarioDTO.class))
                .toList();
    }
}