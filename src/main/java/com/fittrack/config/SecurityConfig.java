package com.fittrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fittrack.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {//Configura la cadena de filtros de seguridad para la aplicación, 
	// definiendo las reglas de autorización y autenticación.
		return http
				.csrf(csrf -> csrf.disable())//Desactiva la protección CSRF, ya que se utilizará JWT para la autenticación.
				.authorizeHttpRequests(authRequest -> authRequest
						.requestMatchers(
								"/auth/login",
								"/auth/register",
								"/swagger-ui.html",
								"/swagger-ui/**",
								"/v3/api-docs/**")
						.permitAll()
						.anyRequest().authenticated())
				.sessionManagement(sessionManager -> sessionManager
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//Configura la política de creación de sesiones como STATELESS, 
						// ya que se utilizará JWT para la autenticación.
				.authenticationProvider(authProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
