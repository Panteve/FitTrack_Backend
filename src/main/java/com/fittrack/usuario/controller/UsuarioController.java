package com.fittrack.usuario.controller;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.usuario.dto.CambiarPasswordRequest;
import com.fittrack.usuario.service.UsuarioService;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> cambiarPassword(
            @RequestBody CambiarPasswordRequest request,// RequestBody para recibir los datos de la solicitud
            @AuthenticationPrincipal UsuarioAutenticado usuario) {// UsuarioAutenticado obtenido del contexto de seguridad

        usuarioService.cambiarPassword(
                usuario,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        return ResponseEntity.noContent().build();
    }

}
