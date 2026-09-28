package com.fittrack.usuario.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

@Service 
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cambiarPassword(
        UsuarioAutenticado usuarioAutenticado,
            String passwordActual,
            String passwordNueva) {

        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(
                passwordActual,
                usuario.getPassword())) {

            throw new RuntimeException(
                    "La contraseña actual es incorrecta");
        }

        usuario.setContrasena(
                passwordEncoder.encode(passwordNueva)
        );

        usuarioRepository.save(usuario);
    }
}
