package com.upc.careme.repositorios;

import com.upc.careme.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTokenRecuperacion(String token);
    Optional<Usuario> findByTelefono(String telefono);
}