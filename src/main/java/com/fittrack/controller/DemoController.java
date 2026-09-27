package com.fittrack.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor

public class DemoController {
	@PostMapping(value = "/demo")
	public String Welcome() {
		return "Login from secure endpoint";
	}
	
}
