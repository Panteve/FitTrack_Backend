package com.fittrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fittrack.jwt.jwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final jwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {  // Filtro relacionado a la autenticación y autorización de solicitudes HTTP.
	//  Configura las reglas de seguridad para la aplicación web.
		return http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authRequest -> 
				authRequest
						.requestMatchers("/auth/login", "/auth/register").permitAll()
						.anyRequest().authenticated()
						)
				.sessionManagement(sessionManager ->
					sessionManager
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authenticationProvider(authProvider)
					.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
 