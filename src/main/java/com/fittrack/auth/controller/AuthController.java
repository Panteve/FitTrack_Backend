package com.fittrack.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.auth.dto.AuthResponseDto;
import com.fittrack.auth.dto.LoginRequestDto;
import com.fittrack.auth.dto.RegisterRequestDto;
import com.fittrack.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "Controlador para la autenticación de usuarios")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthController {

	private final AuthService authService;

	@Operation( summary = "Iniciar sesión")
	@ApiResponses({ 
		@ApiResponse( responseCode = "200", description = "Inicio de sesión exitoso"), 
		@ApiResponse( responseCode = "400", description = "Datos de inicio de sesión inválidos"), 
		@ApiResponse( responseCode = "401", description = "Credenciales incorrectas"),
		@ApiResponse ( responseCode = "500", description = "Error interno del servidor")
	 })
	@PostMapping(value = "/login")
	public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {//endpoint para iniciar sesión
		return ResponseEntity.ok(authService.login(request));//retorna un objeto AuthResponseDto con el token JWT y la información del usuario
	}

	@Operation( summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario y retorna los datos de autenticación.") 
	@ApiResponses({ 
		@ApiResponse( responseCode = "201", description = "Usuario registrado correctamente"), 
		@ApiResponse( responseCode = "400", description = "Datos de registro inválidos"), 
		@ApiResponse( responseCode = "409", description = "El correo electrónico ya está registrado") })
	@PostMapping(value = "/register")
	public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {//endpoint para registrar un nuevo usuario
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(authService.register(request));//retorna un objeto AuthResponseDto con el token JWT y la información del usuario
	}

}
