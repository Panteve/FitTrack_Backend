package com.fittrack.usuario.dto;

/**
 * Respuesta entregada después de guardar una foto de perfil.
 *
 * @param fotoPerfilUrl URL temporal de la fotografía guardada
 */
public record FotoPerfilResponse(String fotoPerfilUrl) {
}
