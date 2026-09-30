package com.fittrack.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {//DTO para la solicitud de inicio de sesión, contiene el correo y la contraseña del usuario

	@NotBlank(message = "El correo es obligatorio")
	String correo;

	@NotBlank(message = "La contrasena es obligatoria")
	String contrasena;
}
