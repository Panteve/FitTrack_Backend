package com.fittrack.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.usuario.dto.CambiarPasswordRequest;
import com.fittrack.usuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "PasswordController", description = "Controlador para cambiar la contraseña del usuario autenticado")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> cambiarPassword(
            @RequestBody CambiarPasswordRequest request,// RequestBody para recibir los datos de la solicitud
            @AuthenticationPrincipal UsuarioAutenticado usuario) {// UsuarioAutenticado obtenido del contexto de seguridad

        usuarioService.cambiarPassword(// Llamada al servicio para cambiar la contraseña
                usuario,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        return ResponseEntity.noContent().build();
    }

}
