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
import com.fittrack.ejercicio.dto.EjerciciosDisponiblesResponse;
import com.fittrack.ejercicio.service.EjercicioService;
import com.fittrack.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

     @Operation(summary = "Listar mis ejercicios", description = "Obtiene todos los ejercicios del usuario autenticado.") 
    @ApiResponse(responseCode = "200", description = "Listado de ejercicios obtenido correctamente")

    @GetMapping("/mis-ejercicios")//endpoint para obtener los ejercicios del usuario autenticado
    public ResponseEntity<List<EjercicioResponse>> obtenerMisEjercicios(
            @AuthenticationPrincipal UsuarioAutenticado usuario) { //usuario autenticado obtenido del JWT
        return ResponseEntity.ok(ejercicioService.listarTodos(usuario.id()));
    }

    /**
     * Lista los ejercicios del sistema y del usuario en grupos separados.
     *
     * @param usuario identidad obtenida del JWT
     * @return ejercicios disponibles para seleccionar
     */
    @GetMapping("/disponibles")
    @Operation(
            summary = "Listar ejercicios disponibles",
            description = "Obtiene por separado los ejercicios del sistema y los creados por el usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Ejercicios disponibles obtenidos")
    public ResponseEntity<EjerciciosDisponiblesResponse> obtenerDisponibles(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                ejercicioService.listarDisponibles(usuario.id()));
    }

    @Operation(summary = "Obtener un ejercicio por ID", description = "Obtiene un ejercicio específico perteneciente al usuario autenticado.") 
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Ejercicio encontrado"), 
        @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado") })

    @GetMapping("/{id}")//endpoint para obtener un ejercicio por su id
    public ResponseEntity<EjercicioResponse> obtenerEjercicioPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                ejercicioService.obtenerPorId(id, usuario.id()));
    }

    @Operation(summary = "Crear un ejercicio", description = "Crea un nuevo ejercicio asociado al usuario autenticado.") 
    @ApiResponses({ 
        @ApiResponse(responseCode = "201", description = "Ejercicio creado correctamente"), 
        @ApiResponse(responseCode = "400", description = "Datos inválidos") })

    @PostMapping///endpoint para crear un nuevo ejercicio
    public ResponseEntity<EjercicioResponse> crearEjercicio(
            @Valid @RequestBody EjercicioRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        EjercicioResponse creado = ejercicioService.guardar(request, usuario.id());
        return ResponseEntity.created(URI.create("/ejercicios/" + creado.id())).body(creado);//retorna un objeto EjercicioResponse con los datos del ejercicio creado 
        // y la ubicación del recurso
    }

    @Operation(summary = "Actualizar un ejercicio", description = "Actualiza los datos de un ejercicio existente del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ejercicio actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })
    @PutMapping("/{id}") //endpoint para actualizar un ejercicio existente
    public ResponseEntity<EjercicioResponse> actualizarEjercicio(
            @PathVariable Long id,
            @Valid @RequestBody EjercicioRequest request, //datos del ejercicio a actualizar
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(ejercicioService.actualizar(id, request, usuario.id()));
    }

    @Operation(summary = "Eliminar un ejercicio", description = "Elimina un ejercicio existente del usuario autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ejercicio eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Ejercicio no encontrado")
    })

    
    //Método para eliminar un ejercicio, se realiza una eliminación lógica en lugar de una eliminación física
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long id, //id del ejercicio a eliminar
            @AuthenticationPrincipal UsuarioAutenticado usuario) { //usuario autenticado que realiza la eliminación
        ejercicioService.logicDelete(id, usuario.id());
        return ResponseEntity.noContent().build();
    }
}
