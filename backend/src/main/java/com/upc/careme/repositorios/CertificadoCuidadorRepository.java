package com.upc.careme.repositorios;

import com.upc.careme.entidades.CertificadoCuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificadoCuidadorRepository extends JpaRepository<CertificadoCuidador, Integer> {
}