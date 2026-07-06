package com.upc.careme.repositorios;

import com.upc.careme.entidades.Cuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CuidadorRepository extends JpaRepository<Cuidador, Integer> {
    java.util.Optional<Cuidador> findByUsuario_IdUsuario(Integer idUsuario);

    @Query("SELECT c FROM Cuidador c WHERE c.activo = true AND " +
           "LOWER(c.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%')) " +
           "ORDER BY c.calificacionPromedio DESC")
    List<Cuidador> findByEspecialidadContainingIgnoreCase(@Param("especialidad") String especialidad);

    @Query("SELECT c FROM Cuidador c WHERE c.activo = true AND " +
           "LOWER(c.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')) " +
           "ORDER BY c.calificacionPromedio DESC")
    List<Cuidador> findByUbicacionContainingIgnoreCase(@Param("ubicacion") String ubicacion);

    @Query("SELECT c FROM Cuidador c WHERE c.activo = true AND " +
           "c.especialidad IS NOT NULL AND c.especialidad <> '' AND " +
           "c.ubicacion IS NOT NULL AND c.ubicacion <> '' AND c.tarifaBase > 0 AND " +
           "(:ubicacion = '' OR LOWER(c.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%'))) AND " +
           "(:especialidad = '' OR LOWER(c.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%'))) AND " +
           "(:disponibilidad = '' OR LOWER(c.disponibilidadTexto) LIKE LOWER(CONCAT('%', :disponibilidad, '%'))) " +
           "ORDER BY c.calificacionPromedio DESC")
    List<Cuidador> buscarPorFiltros(@Param("ubicacion") String ubicacion,
                                    @Param("especialidad") String especialidad,
                                    @Param("disponibilidad") String disponibilidad);

    @Query("SELECT DISTINCT c FROM Cuidador c LEFT JOIN c.condiciones cc WHERE c.activo = true AND " +
           "c.especialidad IS NOT NULL AND c.especialidad <> '' AND " +
           "c.ubicacion IS NOT NULL AND c.ubicacion <> '' AND c.tarifaBase > 0 AND " +
           "(LOWER(c.especialidad) LIKE LOWER(CONCAT('%', :condicion, '%')) OR " +
           " (cc IS NOT NULL AND LOWER(cc.nombreCondicion) LIKE LOWER(CONCAT('%', :condicion, '%')))) " +
           "ORDER BY c.calificacionPromedio DESC")
    List<Cuidador> buscarPorCondicionMedica(@Param("condicion") String condicion);

    @Query("SELECT c FROM Cuidador c WHERE c.activo = true AND " +
           "c.especialidad IS NOT NULL AND c.especialidad <> '' AND " +
           "c.ubicacion IS NOT NULL AND c.ubicacion <> '' AND c.tarifaBase > 0")
    List<Cuidador> findActivosConPerfilCompleto();
}