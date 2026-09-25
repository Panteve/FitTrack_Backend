package com.fittrack.controller;

import com.fittrack.service.RegistroSerieService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registros-serie")
public class RegistroSerieController {

    private final RegistroSerieService registroSerieService;

    public RegistroSerieController(RegistroSerieService registroSerieService) {
        this.registroSerieService = registroSerieService;
    }

}
