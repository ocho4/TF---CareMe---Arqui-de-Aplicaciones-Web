package com.upc.careme.repositorios;

import com.upc.careme.entidades.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Integer> {
    java.util.Optional<TipoUsuario> findByNombreTipo(String nombreTipo);
}