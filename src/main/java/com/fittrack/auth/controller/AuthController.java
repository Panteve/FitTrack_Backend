package com.fittrack.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fittrack.auth.dto.AuthResponseDto;
import com.fittrack.auth.dto.LoginRequestDto;
import com.fittrack.auth.dto.RegisterRequestDto;
import com.fittrack.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping(value = "/login")
	public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping(value = "/register")
	public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(authService.register(request));
	}

}
