package com.fittrack.usuario.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.usuario.dto.UsuarioResponse;
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

    /**
     * Cambia la contraseña del usuario después de verificar la contraseña actual.
     *
     * <p>Las contraseñas nunca se registran en logs ni se incluyen en los mensajes
     * de error para no filtrarlas.</p>
     *
     * @param usuarioAutenticado usuario obtenido del token
     * @param passwordActual contraseña actual sin modificar
     * @param passwordNueva contraseña nueva validada
     * @throws ApiException si el usuario no existe o la contraseña actual es incorrecta
     */
    public void cambiarPassword(
            UsuarioAutenticado usuarioAutenticado,
            String passwordActual,
            String passwordNueva) {

        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        boolean passwordActualCorrecto = passwordEncoder.matches(
                passwordActual,
                usuario.getPassword()
        );

        if (!passwordActualCorrecto) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "La contraseña actual es incorrecta"
            );
        }

        usuario.setContrasena(passwordEncoder.encode(passwordNueva));

        usuarioRepository.save(usuario);
    }

    public void cambiarNombre(UsuarioAutenticado usuarioAutenticado, String nuevoNombre) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));// Buscar el usuario en la base de datos por su ID

        usuario.setNombre(nuevoNombre);// Actualizar el nombre del usuario con el nuevo nombre proporcionado

        usuarioRepository.save(usuario);// Guardar los cambios en la base de datos
    }

    public UsuarioResponse eliminarUsuario(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));// Buscar el usuario en la base de datos por su ID

        usuario.setStatus(false); // Marcar como eliminado

        usuarioRepository.save(usuario);// Guardar los cambios en la base de datos

        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getStatus());// Devolver una respuesta con los datos del usuario eliminado
    }
}
