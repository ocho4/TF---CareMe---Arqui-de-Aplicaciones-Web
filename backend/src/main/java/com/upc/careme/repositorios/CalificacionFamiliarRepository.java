package com.upc.careme.repositorios;

import com.upc.careme.entidades.CalificacionFamiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalificacionFamiliarRepository extends JpaRepository<CalificacionFamiliar, Integer> {

    boolean existsByServicio_IdServicio(Integer idServicio);

    @Query("SELECT c FROM CalificacionFamiliar c " +
           "JOIN FETCH c.cuidador cu JOIN FETCH cu.usuario " +
           "WHERE c.familiar.idFamiliar = :idFamiliar " +
           "ORDER BY c.fechaRegistro DESC")
    List<CalificacionFamiliar> findByFamiliarConCuidador(@Param("idFamiliar") Integer idFamiliar);

    List<CalificacionFamiliar> findByFamiliar_IdFamiliar(Integer idFamiliar);

    @Query("SELECT c.servicio.idServicio FROM CalificacionFamiliar c " +
           "WHERE c.cuidador.idCuidador = :idCuidador")
    List<Integer> findIdServiciosByCuidador(@Param("idCuidador") Integer idCuidador);
}
