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
import com.fittrack.shared.storage.FotoStorageService;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/** Gestiona el registro y la autenticación de usuarios. */
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final FotoStorageService fotoStorageService;
	
	/**
	 * Autentica al usuario y entrega los datos necesarios para iniciar su sesión.
	 *
	 * @param request correo y contraseña enviados por el cliente
	 * @return token, datos básicos y URL temporal de la foto cuando existe
	 */
	public AuthResponseDto login(LoginRequestDto request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getCorreo(), request.getContrasena()));
		Usuario usuario = usuarioRepository.findByCorreoAndStatus(request.getCorreo(), true).orElseThrow(); //Se obtiene el usuario autenticado de la base de datos, 
		// asegurando que su estado sea activo.
		String fotoPerfilUrl = fotoStorageService.obtenerUrlFotoPerfil(
				usuario.getFotoPerfilRuta());
		return AuthResponseDto.builder()
				.token(jwtService.getToken(usuario))
				.usuarioId(usuario.getId())
				.nombre(usuario.getNombre())
				.fotoPerfilUrl(fotoPerfilUrl)
				.build();
	}

	/**
	 * Registra una cuenta y entrega una sesión autenticada sin foto inicial.
	 *
	 * @param request datos validados de la cuenta nueva
	 * @return token y datos básicos del usuario registrado
	 */
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
				.token(jwtService.getToken(usuario))
				.usuarioId(usuario.getId())
				.nombre(usuario.getNombre())
				.fotoPerfilUrl(null)
				.build();
	}
}
