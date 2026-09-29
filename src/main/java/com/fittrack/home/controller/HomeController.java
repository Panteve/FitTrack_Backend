package com.fittrack.home.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.home.dto.HomeDto;
import com.fittrack.home.service.HomeService;
import com.fittrack.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Expone la información principal del usuario autenticado. */
@RestController
@RequestMapping("/home")
@Tag(name = "Home", description = "Información de la pantalla principal")
public class HomeController {

    private final HomeService homeService;

    /**
     * Crea el controlador de la pantalla principal.
     *
     * @param homeService servicio de la pantalla principal
     */
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * Obtiene la rutina sugerida y los entrenamientos recientes del usuario.
     *
     * @param usuario identidad obtenida del JWT
     * @return información de la pantalla principal
     */
    @GetMapping
    @Operation(summary = "Obtener la información del Home")
    @ApiResponse(responseCode = "200", description = "Información obtenida")
    public ResponseEntity<HomeDto> obtenerHome(
            @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.ok(homeService.obtenerHome(usuario.id()));
    }
}
