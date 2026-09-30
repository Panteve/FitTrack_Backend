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
	

	public AuthResponseDto login(LoginRequestDto request) {//Se autentica al usuario utilizando el correo y la contraseña proporcionados en la solicitud de inicio de sesión.
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getCorreo(), request.getContrasena()));
		Usuario usuario = usuarioRepository.findByCorreoAndStatus(request.getCorreo(), true).orElseThrow(); //Se obtiene el usuario autenticado de la base de datos, 
		// asegurando que su estado sea activo.
		String fotoPerfilUrl = fotoStorageService.obtenerUrlFotoPerfil( // Se obtiene la URL de la foto de perfil del usuario autenticado utilizando el servicio de almacenamiento de fotos.
				usuario.getFotoPerfilRuta());
		return AuthResponseDto.builder()//Se construye y retorna un objeto AuthResponseDto que contiene el token JWT generado para el 
		// usuario, su ID, nombre y la URL de su foto de perfil.
				.token(jwtService.getToken(usuario))
				.usuarioId(usuario.getId())
				.nombre(usuario.getNombre())
				.fotoPerfilUrl(fotoPerfilUrl)
				.build();
	}


	@Transactional
	public AuthResponseDto register(RegisterRequestDto request) { //Se registra un nuevo usuario en la base de datos utilizando 
	// los datos proporcionados en la solicitud de registro.
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

		return AuthResponseDto.builder()//Se construye y retorna un objeto AuthResponseDto que contiene el token JWT 
		// generado para el nuevo usuario, su ID, nombre y la URL de su foto de perfil (inicialmente nula).
				.token(jwtService.getToken(usuario))
				.usuarioId(usuario.getId())
				.nombre(usuario.getNombre())
				.fotoPerfilUrl(null)
				.build();
	}
}
