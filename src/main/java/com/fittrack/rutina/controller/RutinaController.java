package com.fittrack.rutina.controller;

import com.fittrack.rutina.service.RutinaService;
import com.fittrack.security.UsuarioAutenticado;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rutinas")
public class RutinaController {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    @GetMapping("/mis-rutinas")
    public String obtenerMisRutinas(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {

        Long usuarioId = usuario.id();
        String nombreUsuario = usuario.correo();

        return "Obteniendo rutinas para el usuario con ID: " + usuarioId;
    }

}
