package com.fittrack.ejercicio.controller;

import com.fittrack.ejercicio.service.EjercicioService;
import com.fittrack.security.UsuarioAutenticado;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ejercicios")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

        @GetMapping("/mis-ejercicios")
    public ResponseEntity<List<EjercicioResponse>> obtenerMisEjercicios(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.listarPorUsuario(usuario.id()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjercicioResponse> obtenerEjercicio(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.obtener(id, usuario.id()));
    }

    @PostMapping
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        EjercicioResponse creado = ejercicioService.crear(request, usuario.id());
        return ResponseEntity.created(URI.create("/ejercicios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request, usuario.id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        ejercicioService.eliminar(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
