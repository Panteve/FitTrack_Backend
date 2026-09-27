package com.fittrack.auth.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.fittrack.auth.dto.AuthResponse;
import com.fittrack.auth.dto.LoginRequest;
import com.fittrack.auth.dto.RegisterRequest;
import com.fittrack.security.JwtService;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final JwtService jwtService;
	
	public AuthResponse login(LoginRequest request) {
		return null;
	}
	
	public AuthResponse register(RegisterRequest request) {
		Usuario usuario = Usuario.builder() //Se crea el objeto usuario utilizando el patrón de diseño Builder. 
		        .nombre(request.getUsername())
		        .correo(request.getCorreo())
		        .contrasena(request.getPassword())
		        .fechaRegistro(LocalDate.now())
		        .build();

		 usuarioRepository.save(usuario);//Se guarda el objeto usuario en la base de datos utilizando el repositorio usuarioRepository.

		 return AuthResponse.builder()
		        .token(jwtService.getToken(usuario)) //se obtiene un token JWT para el usuario recién registrado utilizando el servicio jwtService 
				.build();
	}
}
