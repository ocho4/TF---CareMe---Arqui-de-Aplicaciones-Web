package com.upc.careme.repositorios;

import com.upc.careme.entidades.VerificacionCuidador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificacionCuidadorRepository extends JpaRepository<VerificacionCuidador, Integer> {
    List<VerificacionCuidador> findByEstadoOrderByFechaSolicitudDesc(String estado);
    List<VerificacionCuidador> findAllByOrderByFechaSolicitudDesc();
    Optional<VerificacionCuidador> findTopByCuidadorIdCuidadorOrderByFechaSolicitudDesc(Integer idCuidador);
    long countByEstado(String estado);
}
