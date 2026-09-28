package com.fittrack.rutina.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.rutina.dto.RutinaActualizarDto;
import com.fittrack.rutina.dto.RutinaCrearDto;
import com.fittrack.rutina.dto.RutinaDetalleDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDetalleDto> obtenerRutinaPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.obtenerRutinaPorId(id, usuario.id()));
    }

    @PostMapping
    public ResponseEntity<RutinaDetalleDto> crearRutina(
            @Valid @RequestBody RutinaCrearDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        RutinaDetalleDto rutinaCreada = rutinaService
                .crearRutina(request, usuario.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rutinaCreada);
    }

    /**
     * Reemplaza por completo una rutina del usuario autenticado junto con su
     * configuracion de ejercicios.
     *
     * @param id identificador de la rutina
     * @param request datos nuevos de la rutina y sus ejercicios
     * @param usuario identidad obtenida del JWT
     * @return rutina actualizada con el detalle de sus ejercicios
     */
    @PutMapping("/{id}")
    public ResponseEntity<RutinaDetalleDto> actualizarRutina(
            @PathVariable Long id,
            @Valid @RequestBody RutinaActualizarDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.actualizarRutina(id, request, usuario.id()));
    }

}
