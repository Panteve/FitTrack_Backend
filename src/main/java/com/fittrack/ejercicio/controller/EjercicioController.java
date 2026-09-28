package com.fittrack.ejercicio.controller;

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

import com.fittrack.ejercicio.dto.EjercicioRequest;
import com.fittrack.ejercicio.dto.EjercicioResponse;
import com.fittrack.ejercicio.service.EjercicioService;
import com.fittrack.security.UsuarioAutenticado;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ejercicios")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

    /**
     * Lista los ejercicios pertenecientes al usuario autenticado.
     *
     * @param usuario identidad obtenida del JWT
     * @return ejercicios pertenecientes al usuario
     */
    @GetMapping("/mis-ejercicios")
    public ResponseEntity<List<EjercicioResponse>> obtenerMisEjercicios(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.listarTodos(usuario.id()));
    }

    /**
     * Obtiene un ejercicio perteneciente al usuario autenticado.
     *
     * @param id identificador del ejercicio
     * @param usuario identidad obtenida del JWT
     * @return ejercicio encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<EjercicioResponse> obtenerEjercicioPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                ejercicioService.obtenerPorId(id, usuario.id()));
    }

    /**
     * Crea un ejercicio para el usuario autenticado.
     *
     * @param request datos del ejercicio
     * @param usuario identidad obtenida del JWT
     * @return ejercicio creado
     */
    @PostMapping
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        EjercicioResponse creado = ejercicioService.guardar(request, usuario.id());
        return ResponseEntity.created(URI.create("/ejercicios/" + creado.id())).body(creado);
    }

    /**
     * Actualiza un ejercicio perteneciente al usuario autenticado.
     *
     * @param id identificador del ejercicio
     * @param request nuevos datos del ejercicio
     * @param usuario identidad obtenida del JWT
     * @return ejercicio actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request, usuario.id()));
    }

    /**
     * Elimina un ejercicio perteneciente al usuario autenticado.
     *
     * @param id identificador del ejercicio
     * @param usuario identidad obtenida del JWT
     * @return respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        ejercicioService.logicDelete(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
