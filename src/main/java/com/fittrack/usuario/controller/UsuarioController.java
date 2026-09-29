package com.fittrack.usuario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.security.UsuarioAutenticado;
import com.fittrack.usuario.dto.CambiarNombreRequest;
import com.fittrack.usuario.dto.CambiarPasswordRequest;
import com.fittrack.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "UsuarioController", description = "Controlador para la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @Operation( summary = "Cambiar contraseña", description = "Permite al usuario autenticado cambiar su contraseña actual por una nueva. La nueva debe tener entre 8 y 72 caracteres.") 
    @ApiResponses({ 
        @ApiResponse( responseCode = "204", description = "Contraseña actualizada correctamente"), 
        @ApiResponse( responseCode = "400", description = "Campos vacíos o contraseña nueva fuera del rango permitido de 8 a 72 caracteres"), 
        @ApiResponse( responseCode = "401", description = "La contraseña actual es incorrecta"),
        @ApiResponse( responseCode = "404", description = "El usuario autenticado no existe") })

    @PutMapping("/me/password")
    public ResponseEntity<Void> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,// @Valid evalúa las anotaciones del DTO antes de entrar al servicio
            @AuthenticationPrincipal UsuarioAutenticado usuario) {// UsuarioAutenticado obtenido del contexto de seguridad

        usuarioService.cambiarPassword(// Llamada al servicio para cambiar la contraseña
                usuario,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Cambiar nombre", description = "Permite al usuario autenticado cambiar su nombre actual por uno nuevo.")
    @ApiResponses({
        @ApiResponse( responseCode = "204", description = "Nombre cambiado correctamente"),
        @ApiResponse( responseCode = "400", description = "Datos inválidos"),
        @ApiResponse( responseCode = "401", description = "Usuario no autenticado")
    })
    @PutMapping("/me/nombre")
    public ResponseEntity<Void> cambiarNombre(
            @RequestBody CambiarNombreRequest request,
            @AuthenticationPrincipal UsuarioAutenticado usuario) {

        usuarioService.cambiarNombre(usuario, request.getNuevoNombre());

        return ResponseEntity.noContent().build();
    }

}
