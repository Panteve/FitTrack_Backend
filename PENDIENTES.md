# 📌 Guía de Arquitectura, Pendientes y Referencia Técnica - Spring Boot & Supabase

Este documento sirve como hoja de ruta y referencia técnica para el desarrollo del backend. Contiene las configuraciones clave, la arquitectura esperada y la implementación de seguridad con JWT.

---

## 1. Conexión a Supabase (PostgreSQL)

Supabase opera como una base de datos PostgreSQL estándar. La conexión se realiza mediante Spring Data JPA.

### Requisitos previos

1. Dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Obtener las credenciales en Supabase:

**Project Settings > Database > Connection String (URI / JDBC)**

### Configuración en `src/main/resources/application.properties`

```properties
# Cadena de conexión JDBC (Obligatorio usar sslmode=require)
spring.datasource.url=jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}

# Configuración del Driver
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración de Hibernate / JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **⚠️ Datos Importantes:**
>
> * **SSL Obligatorio:** Si falta el parámetro `?sslmode=require` al final de la URL, Supabase rechazará la conexión.
>
> * **Políticas RLS:** La conexión por JDBC utiliza el usuario administrador `postgres`, omitiendo las políticas Row Level Security (RLS) de Supabase. La seguridad de los datos debe gestionarse en la capa de negocio del backend.
>
> * **Variables de Entorno:** Nunca subir contraseñas al repositorio en GitHub; usar siempre `${DB_PASSWORD}`.

---

## 2. Estructura Estándar del Proyecto

El proyecto sigue una arquitectura en capas (*Layered Architecture*):

```text
src/main/java/com/fittrack/

├── FittrackApplication.java     <-- Clase principal (@SpringBootApplication)
├── config/                      <-- Configuraciones globales (CorsConfig, SecurityConfig, JWT)
├── controller/                 <-- Endpoints HTTP / REST Controllers
├── dto/                        <-- Objetos de transferencia de datos (Request/Response)
├── entity/                     <-- Modelos JPA / Mapeo a tablas de Supabase
├── exception/                 <-- Excepciones personalizadas y @RestControllerAdvice
├── repository/                <-- Interfaces que extienden de JpaRepository
└── service/                   <-- Lógica de negocio e interfaces de servicios
    └── impl/                  <-- Implementación de los servicios
```

---

## 3. Creación e Implementación de DTOs

Los DTOs se utilizan para evitar exponer las `@Entity` de la base de datos directamente hacia el cliente.

### A. Dependencias necesarias para validación (`pom.xml`)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### B. Definición con Java `record`

**Request DTO (Para recibir datos en peticiones POST/PUT):**

```java
package com.fittrack.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    String password

) {}
```

**Response DTO (Para devolver datos al cliente sin datos sensibles):**

```java
package com.fittrack.dto;

public record UsuarioResponseDTO(
    Long id,
    String nombre,
    String email
) {}
```

### C. Uso en el Controller y Service

**Controller:**

```java
@PostMapping
public ResponseEntity<UsuarioResponseDTO> crear(
        @Valid @RequestBody UsuarioRequestDTO request
) {
    return new ResponseEntity<>(
        usuarioService.guardar(request),
        HttpStatus.CREATED
    );
}
```

**Service:**

```java
@Transactional
public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {

    Usuario usuario = new Usuario();

    usuario.setNombre(dto.nombre());
    usuario.setEmail(dto.email());
    usuario.setPassword(passwordEncoder.encode(dto.password()));

    Usuario guardado = usuarioRepository.save(usuario);

    return new UsuarioResponseDTO(
        guardado.getId(),
        guardado.getNombre(),
        guardado.getEmail()
    );
}
```

---

## 4. Configuración de CORS

Permite el consumo de la API desde clientes web (Angular, React, Vue, Svelte, etc.) evitando bloqueos del navegador.

### Ubicación: `com.fittrack.config.CorsConfig`

```java
package com.fittrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "http://localhost:4200",
                            "http://localhost:5173"
                        ) // Rutas de desarrollo frontend
                        .allowedMethods(
                            "GET",
                            "POST",
                            "PUT",
                            "DELETE",
                            "OPTIONS"
                        )
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

---

## 5. Configuración de JWT (Generar y Extraer ID + Correo)

### A. Dependencias de JWT (`pom.xml`)

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

### B. Servicio Generador y Extractor de Token (`JwtService.java`)

En este servicio, el **correo** se asigna como el `Subject` del token, mientras que el **ID del usuario** se guarda como un `Claim` personalizado.

```java
package com.fittrack.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // Clave secreta definida en application.properties
    // (Mínimo 256 bits / 32 caracteres)
    @Value("${jwt.secret:MiClaveSuperSecretaDeAlMenos32CaracteresLongitud12345}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 Horas en milisegundos
    private long jwtExpiration;

    private SecretKey getSigningKey() {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 1. Generar Token incluyendo ID y Correo
    public String generarToken(Long usuarioId, String email) {

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("usuarioId", usuarioId);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(
                    new Date(System.currentTimeMillis() + jwtExpiration)
                )
                .signWith(getSigningKey())
                .compact();
    }

    // 2. Extraer el Correo (Subject)
    public String extraerEmail(String token) {

        return extraerTodosLosClaims(token).getSubject();
    }

    // 3. Extraer el ID del Usuario (Claim personalizado)
    public Long extraerUsuarioId(String token) {

        Claims claims = extraerTodosLosClaims(token);

        return claims.get("usuarioId", Long.class);
    }

    // Validar expiración
    public boolean isTokenValido(String token) {

        try {

            return !extraerTodosLosClaims(token)
                    .getExpiration()
                    .before(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    private Claims extraerTodosLosClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
```

### C. Filtro de Autenticación (`JwtAuthenticationFilter.java`)

Intercepta cada petición para validar el token y cargar el usuario en el contexto de seguridad de Spring.

```java
package com.fittrack.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        if (jwtService.isTokenValido(token)) {

            String email = jwtService.extraerEmail(token);
            Long usuarioId = jwtService.extraerUsuarioId(token);

            // Crear token de autenticación con el correo y el ID del usuario
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.emptyList()
                    );

            // Adjuntar el usuarioId a los detalles de la petición
            // para poder acceder desde los controladores
            authToken.setDetails(usuarioId);

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
```

### D. Configuración de Spring Security (`SecurityConfig.java`)

```java
package com.fittrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
```

### E. Ejemplo: Extraer ID y Correo dentro de un Controller

```java
package com.fittrack.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @GetMapping
    public String obtenerPerfil(Authentication authentication) {

        String email = (String) authentication.getPrincipal();

        Long usuarioId = (Long) authentication.getDetails();

        return "Usuario autenticado -> ID: "
                + usuarioId
                + " | Email: "
                + email;
    }
}
```

---

## 📋 Lista de Verificación (Pendientes)

* [ ] Revisar que la variable de entorno `DB_PASSWORD` esté configurada localmente.
* [ ] Verificar que la interfaz `UsuarioRepository` use `public interface` y extienda `JpaRepository`.
* [ ] Crear el manejador global de excepciones con `@RestControllerAdvice` en `exception/`.
* [ ] Configurar la clave secreta `jwt.secret` en `application.properties`.
* [ ] Probar la extracción del `usuarioId` y `email` mediante un endpoint protegido en Postman enviando el header:

```http
Authorization: Bearer <TOKEN>
```
