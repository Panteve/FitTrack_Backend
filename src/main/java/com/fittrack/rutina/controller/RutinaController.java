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

    @GetMapping
    public ResponseEntity<List<RutinaDto>> obtenerMisRutinas(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {//endpoint para obtener las rutinas del usuario autenticado
        return ResponseEntity.ok(
                rutinaService.obtenerRutinasPorUsuario(usuario.id()));//obtiene las rutinas del usuario autenticado a partir de su id
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDetalleDto> obtenerRutinaPorId(//endpoint para obtener una rutina por su id
            @PathVariable Long id, //path variable que representa el id de la rutina a obtener
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.obtenerRutinaPorId(id, usuario.id()));//obtiene la rutina por su id y el id del usuario autenticado
    }

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


    @PutMapping("/{id}")//endpoint para actualizar una rutina existente
    public ResponseEntity<RutinaDetalleDto> actualizarRutina(
            @PathVariable Long id,
            @Valid @RequestBody RutinaActualizarDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                rutinaService.actualizarRutina(id, request, usuario.id()));//retorna la rutina actualizada con el detalle de sus ejercicios, 
                // llamando al servicio para actualizar la rutina con los datos proporcionados
    }

    /**
     * Desactiva logicamente una rutina del usuario autenticado. La rutina y sus
     * asociaciones se conservan en la base de datos.
     *
     * @param id identificador de la rutina
     * @param usuario identidad obtenida del JWT
     * @return respuesta sin contenido
     */
    @DeleteMapping("/{id}")//endpoint para eliminar una rutina existente
    public ResponseEntity<Void> eliminarRutina(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {//recibe el id de la rutina a eliminar y el usuario autenticado
        rutinaService.eliminarRutina(id, usuario.id());
        return ResponseEntity.noContent().build(); //retorna una respuesta HTTP 204 No Content para indicar que la operación se realizó con éxito,
        //  pero no hay contenido que devolver
    }

}
