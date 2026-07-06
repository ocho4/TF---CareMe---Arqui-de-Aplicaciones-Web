package com.upc.careme.repositorios;

import com.upc.careme.entidades.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    java.util.Optional<Administrador> findByUsuario_IdUsuario(Integer idUsuario);
}