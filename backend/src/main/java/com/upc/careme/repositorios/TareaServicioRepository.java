package com.upc.careme.repositorios;

import com.upc.careme.entidades.TareaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TareaServicioRepository extends JpaRepository<TareaServicio, Integer> {

    List<TareaServicio> findByServicioIdServicioOrderByIdTareaAsc(Integer idServicio);

    // ── Conteo para el dot rojo por servicio ────────────────────────────────

    @Query("SELECT t.servicio.idServicio, COUNT(t) FROM TareaServicio t " +
           "WHERE t.servicio.cuidador.usuario.idUsuario = :idUsuario " +
           "AND t.vistaPorCuidador = false " +
           "GROUP BY t.servicio.idServicio")
    List<Object[]> countNuevasPorServicioCuidador(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT t.servicio.idServicio, COUNT(t) FROM TareaServicio t " +
           "WHERE t.servicio.familiar.usuario.idUsuario = :idUsuario " +
           "AND t.completada = true AND t.vistaPorFamiliar = false " +
           "GROUP BY t.servicio.idServicio")
    List<Object[]> countCompletadasPorServicioFamiliar(@Param("idUsuario") Integer idUsuario);

    // ── Conteo total para notificaciones ────────────────────────────────────

    @Query("SELECT COUNT(t) FROM TareaServicio t " +
           "WHERE t.servicio.cuidador.usuario.idUsuario = :idUsuario " +
           "AND t.vistaPorCuidador = false")
    long countNuevasSinVerByCuidador(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT COUNT(t) FROM TareaServicio t " +
           "WHERE t.servicio.familiar.usuario.idUsuario = :idUsuario " +
           "AND t.completada = true AND t.vistaPorFamiliar = false")
    long countCompletadasSinVerByFamiliar(@Param("idUsuario") Integer idUsuario);

    // ── Marcar como vistas ───────────────────────────────────────────────────

    @Modifying
    @Query("UPDATE TareaServicio t SET t.vistaPorCuidador = true WHERE t.servicio.idServicio = :idServicio")
    void marcarVistoPorCuidador(@Param("idServicio") Integer idServicio);

    @Modifying
    @Query("UPDATE TareaServicio t SET t.vistaPorFamiliar = true WHERE t.servicio.idServicio = :idServicio")
    void marcarVistoPorFamiliar(@Param("idServicio") Integer idServicio);
}
