package com.upc.careme.repositorios;

import com.upc.careme.entidades.Consejo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsejoRepository extends JpaRepository<Consejo, Integer> {
}