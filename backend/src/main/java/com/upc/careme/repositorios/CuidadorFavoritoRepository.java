package com.upc.careme.repositorios;

import com.upc.careme.entidades.CuidadorFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CuidadorFavoritoRepository extends JpaRepository<CuidadorFavorito, Integer> {

    @Query("SELECT f FROM CuidadorFavorito f " +
           "JOIN FETCH f.cuidador c JOIN FETCH c.usuario " +
           "WHERE f.familiar.idFamiliar = :idFamiliar " +
           "ORDER BY f.fechaGuardado DESC")
    List<CuidadorFavorito> findByFamiliarIdFamiliar(@Param("idFamiliar") Integer idFamiliar);

    boolean existsByFamiliar_IdFamiliarAndCuidador_IdCuidador(Integer idFamiliar, Integer idCuidador);

    void deleteByFamiliar_IdFamiliarAndCuidador_IdCuidador(Integer idFamiliar, Integer idCuidador);

    @Query("SELECT f.cuidador.idCuidador FROM CuidadorFavorito f WHERE f.familiar.idFamiliar = :idFamiliar")
    List<Integer> findIdsCuidadoresByFamiliar(@Param("idFamiliar") Integer idFamiliar);
}
