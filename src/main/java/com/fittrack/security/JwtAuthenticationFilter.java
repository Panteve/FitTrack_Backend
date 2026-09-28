package com.fittrack.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) //Metodo para realizar todos los filtros relacionados al token
			throws ServletException, IOException {
		
		final String token = getTokenFromRequest(request);  //Obtener el token del request

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				UsuarioAutenticado usuario = jwtService.getUsuarioAutenticadoFromToken(token);
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						usuario,
						null,
						Collections.emptyList());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} catch (JwtException | IllegalArgumentException exception) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	private String getTokenFromRequest(HttpServletRequest request) { //Metodo que devuelve el token
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {//Extraer el token
			return authHeader.substring(7);
		}
		return null;
	}
	
}
