package com.fittrack.rutina.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.rutina.dto.RutinaDto;
import com.fittrack.rutina.service.RutinaService;
import com.fittrack.security.UsuarioAutenticado;

@RestController
@RequestMapping("/rutinas")
public class RutinaController {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    
    @GetMapping
    public ResponseEntity<List<RutinaDto>> obtenerMisRutinas(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.obtenerRutinasPorUsuario(usuario.id()));
    }
}
