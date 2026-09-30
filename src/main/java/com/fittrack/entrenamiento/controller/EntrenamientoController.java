package com.fittrack.entrenamiento.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fittrack.entrenamiento.dto.EntrenamientoActualizarNotasDto;
import com.fittrack.entrenamiento.dto.EntrenamientoCrearDto;
import com.fittrack.entrenamiento.dto.EntrenamientoDetalleDto;
import com.fittrack.entrenamiento.dto.EntrenamientoDto;
import com.fittrack.entrenamiento.dto.EntrenamientoFotoDto;
import com.fittrack.entrenamiento.service.EntrenamientoService;
import com.fittrack.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

//Expone las operaciones del usuario autenticado sobre sus entrenamientos.
@RestController
@RequestMapping("/entrenamientos")
@Tag(name = "Entrenamientos", description = "CRUD de entrenamientos y fotografías")
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    //Lista los entrenamientos activos del usuario autenticado.
    @GetMapping
    @Operation(summary = "Listar mis entrenamientos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    public ResponseEntity<List<EntrenamientoDto>> obtenerEntrenamientos(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                entrenamientoService.obtenerEntrenamientos(usuario.id()));
    }

    //Obtiene un entrenamiento activo con sus series realizadas.
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un entrenamiento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenamiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado")
    })
    public ResponseEntity<EntrenamientoDetalleDto> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                entrenamientoService.obtenerPorId(id, usuario.id()));
    }

    //Guarda un entrenamiento finalizado.
    @PostMapping
    @Operation(summary = "Finalizar y guardar un entrenamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entrenamiento creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EntrenamientoDetalleDto> crear(
            @Valid @RequestBody EntrenamientoCrearDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entrenamientoService.crear(request, usuario.id()));
    }

    //Actualiza únicamente las notas de un entrenamiento.
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar las notas de un entrenamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrenamiento actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado")
    })
    public ResponseEntity<EntrenamientoDetalleDto> actualizarNotas(
            @PathVariable Long id,
            @Valid @RequestBody EntrenamientoActualizarNotasDto request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                entrenamientoService.actualizarNotas(
                        id,
                        request,
                        usuario.id()));
    }

    //Desactiva lógicamente un entrenamiento.
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lógicamente un entrenamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entrenamiento desactivado"),
            @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        entrenamientoService.eliminar(id, usuario.id());
        return ResponseEntity.noContent().build();
    }

    //Sube o reemplaza la fotografía opcional de un entrenamiento.
    @PostMapping(
            value = "/{id}/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir la fotografía de un entrenamiento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fotografía guardada"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "404", description = "Entrenamiento no encontrado"),
            @ApiResponse(responseCode = "413", description = "Archivo demasiado grande"),
            @ApiResponse(responseCode = "415", description = "Formato no permitido"),
            @ApiResponse(responseCode = "502", description = "Fallo de Supabase Storage")
    })
    public ResponseEntity<EntrenamientoFotoDto> subirFoto(
            @PathVariable Long id,
            @Parameter(
                    description = "Imagen JPEG o PNG, máximo 5 MB",
                    schema = @Schema(type = "string", format = "binary"))
            @RequestPart("foto") MultipartFile foto,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(
                entrenamientoService.subirFoto(id, foto, usuario.id()));
    }
}
