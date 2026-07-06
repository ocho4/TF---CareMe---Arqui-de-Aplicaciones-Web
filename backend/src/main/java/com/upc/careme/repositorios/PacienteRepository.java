package com.upc.careme.repositorios;

import com.upc.careme.entidades.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    java.util.Optional<Paciente> findByUsuario_IdUsuario(Integer idUsuario);
}