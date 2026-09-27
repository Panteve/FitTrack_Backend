package com.fittrack.rutina.controller;

import com.fittrack.rutina.service.RutinaEjercicioService;
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
