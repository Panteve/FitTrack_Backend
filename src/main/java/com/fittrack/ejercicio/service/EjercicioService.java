package com.fittrack.ejercicio.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fittrack.ejercicio.entity.Ejercicio;
import com.fittrack.ejercicio.repository.EjercicioRepository;
import com.fittrack.ejercicio.dto.EjercicioResponse;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    public List<EjercicioResponse> listarTodos(Long usuarioId) {
        return ejercicioRepository.findAll()
        .stream()
        .map(ejercicio -> new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getGrupoMuscular()
        )).toList();
    }

    public Ejercicio buscarPorId(Long id) {
        return ejercicioRepository.findById(id)
                .orElse(null);
    }

    public Ejercicio guardar(Ejercicio ejercicio) {
        return ejercicioRepository.save(ejercicio);
    }

    public Ejercicio actualizar(Long id, Ejercicio ejercicio) {

        Ejercicio ejercicioExistente = ejercicioRepository.findById(id)
                .orElse(null);

        if (ejercicioExistente == null) {
            return null;
        }



        ejercicioExistente.setNombre(ejercicio.getNombre());
        ejercicioExistente.setGrupoMuscular(ejercicio.getGrupoMuscular());

        return ejercicioRepository.save(ejercicioExistente);
    }

}
