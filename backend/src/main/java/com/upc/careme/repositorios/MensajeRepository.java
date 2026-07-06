package com.upc.careme.repositorios;

import com.upc.careme.entidades.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    List<Mensaje> findByServicioIdServicioOrderByFechaEnvioAsc(Integer idServicio);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.servicio.cuidador.idCuidador = :idCuidador AND m.leido = false AND m.remitente.idUsuario <> :idUsuarioCuidador")
    long countMensajesNoLeidosByCuidador(@Param("idCuidador") Integer idCuidador,
                                         @Param("idUsuarioCuidador") Integer idUsuarioCuidador);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.servicio.familiar.usuario.idUsuario = :idUsuario AND m.leido = false AND m.remitente.idUsuario <> :idUsuario")
    long countMensajesNoLeidosByFamiliar(@Param("idUsuario") Integer idUsuario);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.servicio.idServicio = :idServicio AND m.remitente.idUsuario <> :idUsuario")
    void marcarLeidosByServicioAndNoRemitente(@Param("idServicio") Integer idServicio,
                                              @Param("idUsuario") Integer idUsuario);

    @Query("SELECT m.servicio.idServicio, COUNT(m) FROM Mensaje m " +
           "WHERE m.leido = false AND m.remitente.idUsuario != :idUsuario " +
           "AND (m.servicio.familiar.usuario.idUsuario = :idUsuario " +
           "  OR m.servicio.cuidador.usuario.idUsuario = :idUsuario) " +
           "GROUP BY m.servicio.idServicio")
    List<Object[]> countNoLeidosPorServicio(@Param("idUsuario") Integer idUsuario);
}