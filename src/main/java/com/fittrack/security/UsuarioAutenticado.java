package com.fittrack.security;


public record UsuarioAutenticado(Long id, String correo) {// Representa un usuario autenticado con su identificador y correo electrónico.

    public UsuarioAutenticado(Long id, String correo) {
        this.id = id;
        this.correo = correo;
    }
}
