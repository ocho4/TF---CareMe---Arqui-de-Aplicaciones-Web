package com.upc.careme.repositorios;

import com.upc.careme.entidades.RecordatorioMedicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordatorioMedicacionRepository extends JpaRepository<RecordatorioMedicacion, Integer> {
    List<RecordatorioMedicacion> findByPacienteIdPacienteAndTomadoFalse(Integer idPaciente);
}