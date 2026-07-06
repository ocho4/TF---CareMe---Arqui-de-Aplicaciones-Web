package com.upc.careme.repositorios;

import com.upc.careme.entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    boolean existsByServicio_IdServicio(Integer idServicio);

    @Query("SELECT p FROM Pago p " +
           "WHERE p.servicio.familiar.usuario.idUsuario = :idUsuario " +
           "   OR p.servicio.cuidador.usuario.idUsuario = :idUsuario " +
           "ORDER BY p.fechaPago DESC")
    List<Pago> findByUsuario(@Param("idUsuario") Integer idUsuario);
}