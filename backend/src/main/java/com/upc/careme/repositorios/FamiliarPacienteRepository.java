package com.upc.careme.repositorios;

import com.upc.careme.entidades.FamiliarPaciente;
import com.upc.careme.entidades.FamiliarPacienteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamiliarPacienteRepository extends JpaRepository<FamiliarPaciente, FamiliarPacienteId> {
    List<FamiliarPaciente> findByFamiliarIdFamiliar(Integer idFamiliar);
    java.util.Optional<FamiliarPaciente> findByPacienteIdPaciente(Integer idPaciente);
}