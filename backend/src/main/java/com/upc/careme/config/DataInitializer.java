package com.upc.careme.config;

import com.upc.careme.entidades.Administrador;
import com.upc.careme.entidades.TipoUsuario;
import com.upc.careme.entidades.Usuario;
import com.upc.careme.repositorios.AdministradorRepository;
import com.upc.careme.repositorios.TipoUsuarioRepository;
import com.upc.careme.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired private TipoUsuarioRepository tipoUsuarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AdministradorRepository administradorRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // ── Migraciones de columnas ──────────────────────────────────────────
        jdbcTemplate.execute(
            "ALTER TABLE mensajes ADD COLUMN IF NOT EXISTS leido BOOLEAN NOT NULL DEFAULT FALSE"
        );
        // ── Tipos de usuario base ────────────────────────────────────────────
        if (tipoUsuarioRepository.findByNombreTipo("familiar").isEmpty()) {
            TipoUsuario familiar = new TipoUsuario();
            familiar.setNombreTipo("familiar");
            familiar.setDescripcion("Familiar o cliente que contrata el servicio de cuidado");
            tipoUsuarioRepository.save(familiar);
        }
        if (tipoUsuarioRepository.findByNombreTipo("cuidador").isEmpty()) {
            TipoUsuario cuidador = new TipoUsuario();
            cuidador.setNombreTipo("cuidador");
            cuidador.setDescripcion("Profesional de cuidado registrado en la plataforma");
            tipoUsuarioRepository.save(cuidador);
        }
        if (tipoUsuarioRepository.findByNombreTipo("paciente").isEmpty()) {
            TipoUsuario paciente = new TipoUsuario();
            paciente.setNombreTipo("paciente");
            paciente.setDescripcion("Persona que recibe los servicios de cuidado");
            tipoUsuarioRepository.save(paciente);
        }

        // ── Admins ───────────────────────────────────────────────────────────
        // Credenciales (cambia las contraseñas en producción):
        //   carlos.mendoza@careme.admin  /  Admin.CareMe1
        //   sofia.torres@careme.admin    /  Admin.CareMe2
        //   diego.ramirez@careme.admin   /  Admin.CareMe3
        crearAdminSiNoExiste("carlos.mendoza@careme.admin", "Admin.CareMe1", "Carlos",  "Mendoza",  3);
        crearAdminSiNoExiste("sofia.torres@careme.admin",   "Admin.CareMe2", "Sofía",   "Torres",   3);
        crearAdminSiNoExiste("diego.ramirez@careme.admin",  "Admin.CareMe3", "Diego",   "Ramírez",  3);
    }

    private void crearAdminSiNoExiste(String email, String password,
                                      String nombres, String apellidos,
                                      int idTipo) {
        if (usuarioRepository.existsByEmail(email)) return;

        TipoUsuario tipo = new TipoUsuario();
        tipo.setIdTipo(idTipo);

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setRol("admin");
        usuario.setProveedorAuth("local");
        usuario.setTipoUsuario(tipo);
        usuario = usuarioRepository.save(usuario);

        Administrador admin = new Administrador();
        admin.setUsuario(usuario);
        administradorRepository.save(admin);
    }
}
