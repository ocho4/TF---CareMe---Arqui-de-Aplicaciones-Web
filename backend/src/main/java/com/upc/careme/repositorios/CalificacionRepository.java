package com.upc.careme.repositorios;

import com.upc.careme.entidades.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
    List<Calificacion> findByCuidador_IdCuidador(Integer idCuidador);

    @Query("SELECT c FROM Calificacion c JOIN FETCH c.familiar f JOIN FETCH f.usuario WHERE c.cuidador.idCuidador = :idCuidador ORDER BY c.fechaRegistro DESC")
    List<Calificacion> findByCuidadorConFamiliar(@Param("idCuidador") Integer idCuidador);

    boolean existsByServicio_IdServicio(Integer idServicio);
}