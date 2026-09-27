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
