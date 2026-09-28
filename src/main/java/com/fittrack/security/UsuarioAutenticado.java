package com.fittrack.security;

/**
 * Representa la identidad disponible durante una peticion autenticada con JWT.
 *
 * @param id identificador del usuario incluido en el token
 * @param correo correo del usuario incluido en el token
 */
public record UsuarioAutenticado(Long id, String correo) {
}
