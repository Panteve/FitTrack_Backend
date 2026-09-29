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

    /**
     * Lista los ejercicios pertenecientes al usuario autenticado.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return ejercicios pertenecientes al usuario
     */
    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarTodos(Long usuarioId) {
        return ejercicioRepository.findAllByUsuario_IdAndStatusTrue(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lista por separado los ejercicios del sistema y los del usuario.
     *
     * @param usuarioId identificador del usuario autenticado
     * @return ejercicios que puede seleccionar el usuario
     */
    @Transactional(readOnly = true)
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

    /**
     * Obtiene un ejercicio perteneciente al usuario autenticado.
     *
     * @param ejercicioId identificador del ejercicio
     * @param usuarioId identificador del usuario autenticado
     * @return ejercicio encontrado
     * @throws ApiException si el ejercicio no existe o pertenece a otro usuario
     */
    @Transactional(readOnly = true)
    public EjercicioResponse obtenerPorId(Long ejercicioId, Long usuarioId) {
        return toResponse(buscarEjercicioPropio(ejercicioId, usuarioId));
    }

    /**
     * Crea un ejercicio para el usuario autenticado.
     *
     * @param request datos del ejercicio
     * @param usuarioId identificador del usuario autenticado
     * @return ejercicio creado
     */
    @Transactional
    public EjercicioResponse guardar(EjercicioRequest request, Long usuarioId) {
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Ejercicio ejercicio = new Ejercicio(
                usuario,
                request.nombre().trim(),
                request.grupoMuscular().trim(),
                true // Por defecto, el ejercicio está activo
        );

        return toResponse(ejercicioRepository.save(ejercicio));
    }

    /**
     * Actualiza un ejercicio perteneciente al usuario autenticado.
     *
     * @param ejercicioId identificador del ejercicio
     * @param request nuevos datos del ejercicio
     * @param usuarioId identificador del usuario autenticado
     * @return ejercicio actualizado
     * @throws ApiException si el ejercicio no existe o pertenece a otro usuario
     */
    @Transactional
    public EjercicioResponse actualizar(
            Long ejercicioId,
            EjercicioRequest request,
            Long usuarioId) {
        Ejercicio ejercicio = buscarEjercicioPropio(ejercicioId, usuarioId);
        ejercicio.setNombre(request.nombre().trim());
        ejercicio.setGrupoMuscular(request.grupoMuscular().trim());

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

    private Ejercicio buscarEjercicioPropio(Long ejercicioId, Long usuarioId) {
        return ejercicioRepository
                .findByIdAndUsuario_Id(ejercicioId, usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Ejercicio no encontrado."));
    }

    private EjercicioResponse toResponse(Ejercicio ejercicio) {
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getGrupoMuscular());
    }
}
