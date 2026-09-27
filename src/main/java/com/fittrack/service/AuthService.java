package com.fittrack.service;

import org.springframework.stereotype.Service;

import com.fittrack.entity.Usuario;
import com.fittrack.login.AuthResponse;
import com.fittrack.login.LoginRequest;
import com.fittrack.login.RegisterRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	public AuthResponse login(LoginRequest request) {
		return null;
	}
	
	public AuthResponse register(RegisterRequest request) {
		Usuario usuario = Usuario.build()
		        .nombre(request.getNombre())
		        .correo(request.getCorreo())
		        .contrasena(request.getContrasena())
		        .fechaRegistro(LocalDate.now())
		        .build();
	}
}
