package com.fittrack.shared.storage;

/** Administra las fotografías de entrenamientos en almacenamiento externo. */
public interface FotoStorageService {

    /**
     * Sube una fotografía nueva.
     *
     * @param ruta ruta interna única
     * @param contenido bytes de la imagen
     * @param contentType tipo MIME validado
     */
    void subir(String ruta, byte[] contenido, String contentType);

    /**
     * Genera una URL temporal para consultar una fotografía privada.
     *
     * @param ruta ruta interna almacenada en la base de datos
     * @return URL firmada temporal
     */
    String generarUrlFirmada(String ruta);

    /**
     * Busca la foto de perfil de un usuario y genera una URL temporal si existe.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return URL firmada o {@code null} cuando el usuario no tiene foto
     */
    String obtenerUrlFotoPerfil(Long usuarioId);

    /**
     * Intenta eliminar una fotografía que ya no se utiliza.
     *
     * @param ruta ruta interna de la fotografía
     */
    void eliminar(String ruta);
}
