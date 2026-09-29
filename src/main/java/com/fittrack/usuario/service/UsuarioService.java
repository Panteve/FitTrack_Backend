package com.fittrack.usuario.service;


import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.shared.storage.FotoStorageService;
import com.fittrack.usuario.dto.FotoPerfilResponse;
import com.fittrack.usuario.dto.UsuarioResponse;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;




/** Gestiona los datos y operaciones de la cuenta autenticada. */
@Service
public class UsuarioService {

    private static final long TAMANO_MAXIMO_FOTO = 5L * 1024L * 1024L;
    private static final Set<String> TIPOS_FOTO_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png");
    private static final Map<String, String> EXTENSIONES_FOTO = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FotoStorageService fotoStorageService;

    /**
     * Crea el servicio de usuarios con sus dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param passwordEncoder codificador seguro de contraseñas
     * @param fotoStorageService almacenamiento externo de fotografías
     */
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            FotoStorageService fotoStorageService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.fotoStorageService = fotoStorageService;
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

    /**
     * Valida y guarda la foto de perfil del usuario autenticado.
     *
     * @param usuarioAutenticado usuario obtenido del token
     * @param foto archivo JPEG o PNG recibido por multipart
     * @return URL temporal de la foto guardada
     * @throws ApiException si el usuario o la fotografía no son válidos
     */
    @Transactional
    public FotoPerfilResponse guardarFotoPerfil(
            UsuarioAutenticado usuarioAutenticado,
            MultipartFile foto) {
        Usuario usuario = buscarUsuario(usuarioAutenticado.id());
        String contentType = validarFoto(foto);
        String rutaAnterior = usuario.getFotoPerfilRuta();
        String rutaNueva = construirRutaFotoPerfil(
                usuario.getId(),
                EXTENSIONES_FOTO.get(contentType));

        byte[] contenido;
        try {
            contenido = foto.getBytes();
        } catch (IOException ex) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "No fue posible leer la fotografía enviada.");
        }

        String fotoPerfilUrl = fotoStorageService.guardarFotoPerfil(
                rutaNueva,
                contenido,
                contentType);

        try {
            usuario.setFotoPerfilRuta(rutaNueva);
            usuarioRepository.saveAndFlush(usuario);
        } catch (RuntimeException error) {
            fotoStorageService.eliminarFotoPerfil(rutaNueva);
            throw error;
        }

        fotoStorageService.eliminarFotoPerfil(rutaAnterior);
        return new FotoPerfilResponse(fotoPerfilUrl);
    }

    /**
     * Elimina la foto de perfil del usuario autenticado si existe.
     *
     * @param usuarioAutenticado usuario obtenido del token
     * @throws ApiException si el usuario no existe
     */
    @Transactional
    public void eliminarFotoPerfil(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = buscarUsuario(usuarioAutenticado.id());
        String rutaAnterior = usuario.getFotoPerfilRuta();

        if (rutaAnterior == null || rutaAnterior.isBlank()) {
            return;
        }

        usuario.setFotoPerfilRuta(null);
        usuarioRepository.saveAndFlush(usuario);
        fotoStorageService.eliminarFotoPerfil(rutaAnterior);
    }

    public UsuarioResponse eliminarUsuario(UsuarioAutenticado usuarioAutenticado) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.id())
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));// Buscar el usuario en la base de datos por su ID

        usuario.setStatus(false); // Marcar como eliminado

        usuarioRepository.save(usuario);// Guardar los cambios en la base de datos

        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getStatus());// Devolver una respuesta con los datos del usuario eliminado
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"));
    }

    private String validarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografía no puede estar vacía.");
        }
        if (foto.getSize() > TAMANO_MAXIMO_FOTO) {
            throw new ApiException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "La fotografía no puede superar 5 MB.");
        }
        String contentType = foto.getContentType();
        if (contentType == null || !TIPOS_FOTO_PERMITIDOS.contains(contentType)) {
            throw new ApiException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "La fotografía debe estar en formato JPEG o PNG.");
        }
        return contentType;
    }

    private String construirRutaFotoPerfil(Long usuarioId, String extension) {
        return "usuarios/" + usuarioId + "/perfil/"
                + UUID.randomUUID() + extension;
    }
}
