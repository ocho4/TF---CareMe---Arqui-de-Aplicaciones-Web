package com.upc.careme.repositorios;

import com.upc.careme.entidades.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByPacienteIdPaciente(Integer idPaciente);

    @Query("SELECT s FROM Servicio s WHERE " +
           "(s.familiar.usuario.idUsuario = :idUsuario OR s.cuidador.usuario.idUsuario = :idUsuario) AND " +
           "s.estado = 'CONFIRMADO' AND s.fechaInicio BETWEEN :ahora AND :limite")
    List<Servicio> findServiciosProximos(@Param("idUsuario") Integer idUsuario,
                                         @Param("ahora") LocalDateTime ahora,
                                         @Param("limite") LocalDateTime limite);

    @Query("SELECT s FROM Servicio s WHERE " +
           "(s.familiar.usuario.idUsuario = :idUsuario OR s.cuidador.usuario.idUsuario = :idUsuario) AND " +
           "s.estado = 'CONFIRMADO' AND s.fechaInicio >= :fechaInicio AND s.fechaInicio <= :fechaFin")
    List<Servicio> findByAgenda(@Param("idUsuario") Integer idUsuario,
                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT s FROM Servicio s WHERE s.estado = 'CONFIRMADO' AND s.fechaInicio BETWEEN :ahora AND :limite")
    List<Servicio> findServiciosParaAlerta(@Param("ahora") LocalDateTime ahora,
                                           @Param("limite") LocalDateTime limite);

    @Query("SELECT s FROM Servicio s " +
           "LEFT JOIN FETCH s.familiar f LEFT JOIN FETCH f.usuario " +
           "LEFT JOIN FETCH s.cuidador c LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH s.paciente p LEFT JOIN FETCH p.usuario " +
           "WHERE s.idServicio = :id")
    Optional<Servicio> findByIdCompleto(@Param("id") Integer id);

    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.familiar f LEFT JOIN FETCH f.usuario " +
           "LEFT JOIN FETCH s.cuidador c LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH s.paciente p LEFT JOIN FETCH p.usuario")
    List<Servicio> findAllCompleto();

    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.familiar f LEFT JOIN FETCH f.usuario " +
           "LEFT JOIN FETCH s.cuidador c LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH s.paciente p LEFT JOIN FETCH p.usuario " +
           "WHERE s.familiar.usuario.idUsuario = :idUsuario OR s.cuidador.usuario.idUsuario = :idUsuario")
    List<Servicio> findByUsuarioCompleto(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT DISTINCT s FROM Servicio s " +
           "LEFT JOIN FETCH s.familiar f LEFT JOIN FETCH f.usuario " +
           "LEFT JOIN FETCH s.cuidador c LEFT JOIN FETCH c.usuario " +
           "LEFT JOIN FETCH s.paciente p LEFT JOIN FETCH p.usuario " +
           "WHERE c.usuario.idUsuario = :idUsuario AND s.estado = 'SOLICITADO'")
    List<Servicio> findPendientesByCuidadorUsuario(@Param("idUsuario") Integer idUsuario);

    long countByCuidadorIdCuidadorAndEstado(Integer idCuidador, String estado);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.cuidador.idCuidador = :idCuidador AND s.estado = 'CONFIRMADO' AND s.fechaInicio BETWEEN :ahora AND :limite")
    long countServiciosProximosCuidador(@Param("idCuidador") Integer idCuidador,
                                        @Param("ahora") LocalDateTime ahora,
                                        @Param("limite") LocalDateTime limite);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.familiar.usuario.idUsuario = :idUsuario AND s.estado = 'CONFIRMADO' AND s.fechaInicio BETWEEN :ahora AND :limite")
    long countServiciosProximosFamiliar(@Param("idUsuario") Integer idUsuario,
                                        @Param("ahora") LocalDateTime ahora,
                                        @Param("limite") LocalDateTime limite);

    @Query("SELECT COUNT(s) FROM Servicio s WHERE s.familiar.usuario.idUsuario = :idUsuario AND s.estado = 'CONFIRMADO' AND s.fechaFin <= :ahora")
    long countServiciosConPagoPendienteFamiliar(@Param("idUsuario") Integer idUsuario,
                                               @Param("ahora") LocalDateTime ahora);
}