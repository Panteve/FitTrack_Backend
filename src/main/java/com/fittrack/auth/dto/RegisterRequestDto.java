package com.fittrack.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {//DTO para la solicitud de registro, contiene el nombre, correo y la contraseña del usuario

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 255, message = "El nombre no puede superar los 255 caracteres")
	String nombre;

	@NotBlank(message = "La contrasena es obligatoria")
	// BCrypt solo considera los primeros 72 bytes, por eso se limita el largo de entrada.
	@Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
	String contrasena;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo tiene un formato invalido")
	@Size(max = 255, message = "El correo no puede superar los 255 caracteres")
	String correo;
}
