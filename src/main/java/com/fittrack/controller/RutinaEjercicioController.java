package com.fittrack.controller;

import com.fittrack.service.RutinaEjercicioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rutinas-ejercicios")
public class RutinaEjercicioController {

    private final RutinaEjercicioService rutinaEjercicioService;

    public RutinaEjercicioController(RutinaEjercicioService rutinaEjercicioService) {
        this.rutinaEjercicioService = rutinaEjercicioService;
    }

}
