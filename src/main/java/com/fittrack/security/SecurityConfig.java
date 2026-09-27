package com.fittrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.fittrack.jwt.jwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {  // Metodo para restringir el acceso a las rutas
		jwtAuthenticationFilter jwtAuthenticationFilter;
		return http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authRequest -> 
				authRequest
						.requestMatchers("/auth/login").permitAll()
						.anyRequest().authenticated()
						)
				.sessionManagement(sessionManager ->
					sessionManager
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authenticationProvider(authProvider)
					.addFilterBefore(jwtAuthenticationFilter, )
				.build();
	}
}
