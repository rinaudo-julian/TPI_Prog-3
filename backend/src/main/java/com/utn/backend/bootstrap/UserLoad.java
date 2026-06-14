package com.utn.backend.bootstrap;

import com.utn.backend.enums.Rol;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoad implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Admin");
            admin.setEmail("admin@admin.com");
            admin.setContrasena(passwordEncoder.encode("123456"));
            admin.setRol(Rol.ADMIN);

            usuarioRepository.save(admin);
            log.info("Usuario admin creado por defecto: {}", admin.getEmail());
        }
    }
}
