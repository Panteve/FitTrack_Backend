package com.fittrack.auth.service;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fittrack.auth.dto.AuthResponseDto;
import com.fittrack.auth.dto.LoginRequestDto;
import com.fittrack.auth.dto.RegisterRequestDto;
import com.fittrack.security.JwtService;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public AuthResponseDto login(LoginRequestDto request) { //Metodo que maneja la lógica de inicio de sesión.
	//  Recibe un objeto LoginRequestDto que contiene el correo y la contraseña del usuario.
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getCorreo(), request.getContrasena()));
		Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();
		String token = jwtService.getToken(usuario);
		return AuthResponseDto.builder()
				.token(token)
				.nombre(usuario.getNombre())
				.build();			

	}
	

	@Transactional
	public AuthResponseDto register(RegisterRequestDto request) {
		if (usuarioRepository.existsByCorreo(request.getCorreo())) {
			throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
		}
		Usuario usuario = Usuario.builder() //Se crea el objeto usuario utilizando el patrón de diseño Builder. 
		        .nombre(request.getNombre())
		        .correo(request.getCorreo())
		        .contrasena(passwordEncoder.encode(request.getContrasena()))
		        .fechaRegistro(LocalDate.now())
		        .build();

		try {
			usuarioRepository.saveAndFlush(usuario);
		} catch (DataIntegrityViolationException exception) {
			throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
		}

		 return AuthResponseDto.builder()
		        .token(jwtService.getToken(usuario)) //se obtiene un token JWT para el usuario recién registrado utilizando el servicio jwtService 
				.build();
	}
}
