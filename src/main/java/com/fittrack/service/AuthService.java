package com.fittrack.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.fittrack.entity.Usuario;
import com.fittrack.login.AuthResponse;
import com.fittrack.login.LoginRequest;
import com.fittrack.login.RegisterRequest;
import com.fittrack.repository.UsuarioRepository;

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
		Usuario usuario = Usuario.builder()
		        .nombre(request.getUsername())
		        .correo(request.getCorreo())
		        .contrasena(request.getPassword())
		        .fechaRegistro(LocalDate.now())
		        .build();

		 usuarioRepository.save(usuario);

		 return AuthResponse.builder()
		        .token(jwtService.getToken(usuario))
				.build();
	}
}
