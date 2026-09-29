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

    public void cambiarPassword(UsuarioAutenticado usuarioAutenticado,String passwordActual,String passwordNueva) {

        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));// Buscar el usuario en la base de datos por su ID

        if (!passwordEncoder.matches(
                passwordActual,
                usuario.getPassword())) {// Verificar si la contraseña actual proporcionada coincide con la almacenada en la base de datos

            throw new RuntimeException(
                    "La contraseña actual es incorrecta");
        }

        usuario.setContrasena(// Actualizar la contraseña del usuario con la nueva contraseña codificada
                passwordEncoder.encode(passwordNueva)
        );

        usuarioRepository.save(usuario);// Guardar los cambios en la base de datos
    }

    public void cambiarNombre(UsuarioAutenticado usuarioAutenticado, String nuevoNombre) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));// Buscar el usuario en la base de datos por su ID

        usuario.setNombre(nuevoNombre);// Actualizar el nombre del usuario con el nuevo nombre proporcionado

        usuarioRepository.save(usuario);// Guardar los cambios en la base de datos
    }
}
