package com.upc.careme.repositorios;

import com.upc.careme.entidades.HorarioCuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioCuidadorRepository extends JpaRepository<HorarioCuidador, Integer> {
}