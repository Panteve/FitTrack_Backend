package com.fittrack.rutina.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fittrack.rutina.dto.RutinaDetalleDto;
import com.fittrack.rutina.dto.RutinaDto;
import com.fittrack.rutina.dto.RutinaEjercicioDetalleDto;
import com.fittrack.rutina.entity.Rutina;
import com.fittrack.rutina.repository.RutinaRepository;
import com.fittrack.shared.exception.ApiException;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;

    public RutinaService(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> obtenerRutinasPorUsuario(Long usuarioId) {
        return rutinaRepository.findDistinctByUsuario_Id(usuarioId)
                .stream()
                .map(rutina -> new RutinaDto(
                        rutina.getId(),
                        rutina.getNombre(),
                        rutina.getDescripcion(),
                        rutina.getDiaSemana(),
                        rutina.getRutinaEjercicios()
                                .stream()
                                .map(rutinaEjercicio -> rutinaEjercicio
                                        .getEjercicio()
                                        .getNombre())
                                .toList()))
                .toList();
    }

    @Transactional(readOnly = true)
    public RutinaDetalleDto obtenerRutinaPorId(Long rutinaId, Long usuarioId) {
        return rutinaRepository
                .findDistinctByIdAndUsuario_Id(rutinaId, usuarioId)
                .map(this::toDetalleDto)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Rutina no encontrada."));
    }

    private RutinaDetalleDto toDetalleDto(Rutina rutina) {
        List<RutinaEjercicioDetalleDto> ejercicios = rutina
                .getRutinaEjercicios()
                .stream()
                .map(rutinaEjercicio -> new RutinaEjercicioDetalleDto(
                        rutinaEjercicio.getId(),
                        rutinaEjercicio.getEjercicio().getId(),
                        rutinaEjercicio.getEjercicio().getNombre(),
                        rutinaEjercicio.getSeriesObjetivo(),
                        rutinaEjercicio.getRepeticionesObjetivo(),
                        rutinaEjercicio.getPesoObjetivo(),
                        rutinaEjercicio.getOrden()))
                .toList();

        return new RutinaDetalleDto(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                rutina.getDiaSemana(),
                ejercicios);
    }
}
