package com.fittrack.rutina.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.fittrack.rutina.dto.RutinaActualizarDto;
import com.fittrack.rutina.dto.RutinaCrearDto;
import com.fittrack.rutina.dto.RutinaDetalleDto;
import com.fittrack.rutina.dto.RutinaDto;
import com.fittrack.rutina.service.RutinaService;
import com.fittrack.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rutinas")
@Tag(name = "Rutinas", description = "CRUD de rutinas")
public class RutinaController {

    private final RutinaService rutinaService;

    public RutinaController(RutinaService rutinaService) {
        this.rutinaService = rutinaService;
    }

    @Operation(
            summary = "Listar mis rutinas",
            description = "Obtiene las rutinas del usuario autenticado, de la más reciente a la más antigua.")
    @ApiResponse(responseCode = "200", description = "Listado de rutinas obtenido correctamente")
    @GetMapping
    public ResponseEntity<List<RutinaDto>> obtenerMisRutinas(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {//endpoint para obtener las rutinas del usuario autenticado
        return ResponseEntity.ok(
                rutinaService.obtenerRutinasPorUsuario(usuario.id()));//obtiene las rutinas del usuario autenticado a partir de su id
    }


    @Operation(summary = "Obtener una rutina por ID", description = "Obtiene el detalle de una rutina perteneciente al usuario autenticado.")
     @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Rutina encontrada"),
        @ApiResponse(responseCode = "404", description = "Rutina no encontrada") })

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDetalleDto> obtenerRutinaPorId(//endpoint para obtener una rutina por su id
            @PathVariable Long id, //path variable que representa el id de la rutina a obtener
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.obtenerRutinaPorId(id, usuario.id()));//obtiene la rutina por su id y el id del usuario autenticado
    }


    @Operation(summary = "Crear una rutina", description = "Crea una nueva rutina para el usuario autenticado.") 
    @ApiResponses({ 
        @ApiResponse(responseCode = "201", description = "Rutina creada correctamente"), 
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe una rutina con el mismo nombre") })

    @PostMapping
    public ResponseEntity<RutinaDetalleDto> crearRutina(
            @Valid @RequestBody RutinaCrearDto request,//recibe la solicitud de creación de rutina y valida los datos del request body
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        RutinaDetalleDto rutinaCreada = rutinaService
                .crearRutina(request, usuario.id());// recibe la solicitud de creación de rutina y el usuario autenticado,
                                                //  y llama al servicio para crear la rutina
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rutinaCreada);
    }



    @Operation(summary = "Actualizar una rutina", description = "Actualiza una rutina existente perteneciente al usuario autenticado.") 
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Rutina actualizada correctamente"), 
        @ApiResponse(responseCode = "400", description = "Datos inválidos"), 
        @ApiResponse(responseCode = "404", description = "Rutina no encontrada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una rutina con el mismo nombre") })

    @PutMapping("/{id}")//endpoint para actualizar una rutina existente
    public ResponseEntity<RutinaDetalleDto> actualizarRutina(
            @PathVariable Long id,
            @Valid @RequestBody RutinaActualizarDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.actualizarRutina(id, request, usuario.id()));//retorna la rutina actualizada con el detalle de sus ejercicios, 
                // llamando al servicio para actualizar la rutina con los datos proporcionados
    }


    @Operation(summary = "Eliminar una rutina", description = "Desactiva lógicamente una rutina perteneciente al usuario autenticado.") 
    @ApiResponses({ 
        @ApiResponse(responseCode = "204", description = "Rutina eliminada correctamente"), 
        @ApiResponse(responseCode = "404", description = "Rutina no encontrada") })
        
    @DeleteMapping("/{id}")//endpoint para eliminar una rutina existente
    public ResponseEntity<Void> eliminarRutina(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {//recibe el id de la rutina a eliminar y el usuario autenticado
        rutinaService.eliminarRutina(id, usuario.id());
        return ResponseEntity.noContent().build(); //retorna una respuesta HTTP 204 No Content para indicar que la operación se realizó con éxito,
        //  pero no hay contenido que devolver
    }

}
