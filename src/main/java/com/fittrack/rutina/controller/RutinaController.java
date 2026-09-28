package com.fittrack.rutina.controller;

import com.fittrack.rutina.service.RutinaService;
import com.fittrack.usuario.entity.Usuario;

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
            @AuthenticationPrincipal Usuario usuario) {

        Long usuarioId = usuario.getId();

        return "Obteniendo rutinas para el usuario con ID: " + usuarioId;
    }

}
