package com.fittrack.ejercicio.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fittrack.ejercicio.dto.EjercicioRequest;
import com.fittrack.ejercicio.dto.EjercicioResponse;
import com.fittrack.ejercicio.dto.EjerciciosDisponiblesResponse;
import com.fittrack.ejercicio.entity.Ejercicio;
import com.fittrack.ejercicio.repository.EjercicioRepository;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

@Service
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;


    public EjercicioService(
            EjercicioRepository ejercicioRepository,
            UsuarioRepository usuarioRepository) {
        this.ejercicioRepository = ejercicioRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Transactional(readOnly = true)// Lista todos los ejercicios activos del usuario autenticado.
    public List<EjercicioResponse> listarTodos(Long usuarioId) {
        return ejercicioRepository.findAllByUsuario_IdAndStatusTrue(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional(readOnly = true)// Lista todos los ejercicios activos del sistema y del usuario autenticado.
    public EjerciciosDisponiblesResponse listarDisponibles(Long usuarioId) {
        List<EjercicioResponse> ejerciciosSistema = ejercicioRepository
                .findAllByUsuarioIsNullAndStatusTrueOrderByNombreAsc()
                .stream()
                .map(this::toResponse)
                .toList();
        List<EjercicioResponse> misEjercicios = ejercicioRepository
                .findAllByUsuario_IdAndStatusTrueOrderByNombreAsc(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();

        return new EjerciciosDisponiblesResponse(
                ejerciciosSistema,
                misEjercicios);
    }


    @Transactional(readOnly = true)//
    public EjercicioResponse obtenerPorId(Long ejercicioId, Long usuarioId) {// Obtiene un ejercicio activo del usuario autenticado por su identificador.
        return toResponse(buscarEjercicioPropio(ejercicioId, usuarioId));
    }


    @Transactional
    public EjercicioResponse guardar(EjercicioRequest request, Long usuarioId) {// Guarda un nuevo ejercicio para el usuario autenticado.
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Ejercicio ejercicio = new Ejercicio(
                usuario,
                request.nombre().trim(),
                request.grupoMuscular(),
                true // Por defecto, el ejercicio está activo
        );

        return toResponse(ejercicioRepository.save(ejercicio));
    }

    @Transactional
    public EjercicioResponse actualizar(// Actualiza un ejercicio propio del usuario autenticado.
            Long ejercicioId,
            EjercicioRequest request,
            Long usuarioId) {
        Ejercicio ejercicio = buscarEjercicioPropio(ejercicioId, usuarioId);
        ejercicio.setNombre(request.nombre().trim());
        ejercicio.setGrupoMuscular(request.grupoMuscular());

        return toResponse(ejercicio);
    }


    @Transactional
    public EjercicioResponse logicDelete( // Método para realizar una eliminación lógica de un ejercicio
            Long ejercicioId,
            Long usuarioId) {
        Ejercicio ejercicio = buscarEjercicioPropio(ejercicioId, usuarioId);

        if(ejercicio == null) {
            return null;
        }
        ejercicio.setStatus(false); // Marcar como eliminado

        return toResponse(ejercicio);
    }

    private Ejercicio buscarEjercicioPropio(Long ejercicioId, Long usuarioId) {// Busca un ejercicio activo del usuario autenticado por su identificador.
        return ejercicioRepository
                .findByIdAndUsuario_Id(ejercicioId, usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Ejercicio no encontrado."));
    }

    private EjercicioResponse toResponse(Ejercicio ejercicio) { // Método para convertir un objeto Ejercicio a EjercicioResponse
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getGrupoMuscular());
    }
}
