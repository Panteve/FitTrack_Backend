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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ejercicios")
@Tag(name = "Ejercicios", description = "CRUD de ejercicios")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

 
    @GetMapping("/mis-ejercicios")//endpoint para obtener los ejercicios del usuario autenticado
    public ResponseEntity<List<EjercicioResponse>> obtenerMisEjercicios(
            @AuthenticationPrincipal UsuarioAutenticado usuario) { //usuario autenticado obtenido del JWT
        return ResponseEntity.ok(ejercicioService.listarTodos(usuario.id()));
    }


    @GetMapping("/{id}")//endpoint para obtener un ejercicio por su id
    public ResponseEntity<EjercicioResponse> obtenerEjercicioPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                ejercicioService.obtenerPorId(id, usuario.id()));
    }


    @PostMapping///endpoint para crear un nuevo ejercicio
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        EjercicioResponse creado = ejercicioService.guardar(request, usuario.id());
        return ResponseEntity.created(URI.create("/ejercicios/" + creado.id())).body(creado);
    }


    @PutMapping("/{id}") //endpoint para actualizar un ejercicio existente
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request, //datos del ejercicio a actualizar
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request, usuario.id()));
    }

    //Método para eliminar un ejercicio, se realiza una eliminación lógica en lugar de una eliminación física
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long id, //id del ejercicio a eliminar
            @AuthenticationPrincipal UsuarioAutenticado usuario) { //usuario autenticado que realiza la eliminación
        ejercicioService.logicDelete(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
