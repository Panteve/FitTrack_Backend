package com.fittrack.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Datos que recibe el cliente después de autenticarse o registrarse. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
	private String token;
	private Long usuarioId;
	private String nombre;
	private String fotoPerfilUrl;
}
