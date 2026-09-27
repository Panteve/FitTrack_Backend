package com.fittrack.jwt;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class jwtAuthenticationFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) //Metodo para realizar todos los filtros relacionados al token
			throws ServletException, IOException {
		
		final String token = getTokenFromRequest(request);  //Obtener el token del request
			
			if (token == null) {
				filterChain.doFilter(request, response);
				return;
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
