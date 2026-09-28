package com.fittrack.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configura la documentación OpenAPI y la autenticación JWT de Swagger UI.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FitTrack API",
                version = "v1",
                description = "API para administrar usuarios, ejercicios y rutinas."),
        security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
@SecurityScheme(
        name = OpenApiConfig.BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT obtenido desde POST /auth/login.")
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
}
