package com.fittrack.rutina.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fittrack.ejercicio.entity.Ejercicio;
import com.fittrack.ejercicio.repository.EjercicioRepository;
import com.fittrack.rutina.dto.RutinaActualizarDto;
import com.fittrack.rutina.dto.RutinaCrearDto;
import com.fittrack.rutina.dto.RutinaDetalleDto;
import com.fittrack.rutina.dto.RutinaDto;
import com.fittrack.rutina.dto.RutinaEjercicioCrearDto;
import com.fittrack.rutina.dto.RutinaEjercicioDetalleDto;
import com.fittrack.rutina.dto.RutinaSerieDetalleDto;
import com.fittrack.rutina.entity.Rutina;
import com.fittrack.rutina.entity.RutinaEjercicio;
import com.fittrack.rutina.entity.RutinaSerie;
import com.fittrack.rutina.repository.RutinaRepository;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final UsuarioRepository usuarioRepository;

    public RutinaService(
            RutinaRepository rutinaRepository,
            EjercicioRepository ejercicioRepository,
            UsuarioRepository usuarioRepository) {
        this.rutinaRepository = rutinaRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> obtenerRutinasPorUsuario(Long usuarioId) {
        return rutinaRepository
                .findDistinctByUsuario_IdAndStatusTrueOrderByIdDesc(usuarioId)
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
                .findDistinctByIdAndUsuario_IdAndStatusTrue(rutinaId, usuarioId)
                .map(this::toDetalleDto)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Rutina no encontrada."));
    }

 
    @Transactional
    public RutinaDetalleDto crearRutina(// Crea una nueva rutina para un usuario autenticado.
            RutinaCrearDto request,
            Long usuarioId) {
        String nombreNormalizado = request.nombre().trim();
        validarNombreDisponibleParaCrear(usuarioId, nombreNormalizado);

        Map<Long, Ejercicio> ejerciciosPorId = buscarEjercicios(
                request.ejercicios());

        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Rutina rutina = new Rutina(
                usuario,
                nombreNormalizado,
                normalizarDescripcion(request.descripcion()),
                request.diaSemana());

        List<RutinaEjercicio> asociaciones = construirAsociaciones(
                request.ejercicios(),
                ejerciciosPorId);
        rutina.reemplazarRutinaEjercicios(asociaciones);

        Rutina rutinaGuardada = rutinaRepository.saveAndFlush(rutina);

        return toDetalleDto(rutinaGuardada);
    }


    @Transactional
    public RutinaDetalleDto actualizarRutina(
            Long rutinaId,
            RutinaActualizarDto request,
            Long usuarioId) {
        // Se validan los ejercicios antes de tocar la rutina: si falta alguno,
        // la transaccion falla sin haber modificado nada.
        Map<Long, Ejercicio> ejerciciosPorId = buscarEjercicios(
                request.ejercicios());

        Rutina rutina = rutinaRepository
                .findByIdAndUsuario_IdAndStatusTrue(rutinaId, usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Rutina no encontrada."));

        String nombreNormalizado = request.nombre().trim();
        validarNombreDisponibleParaActualizar(
                usuarioId,
                nombreNormalizado,
                rutinaId);

        rutina.setNombre(nombreNormalizado);
        rutina.setDescripcion(normalizarDescripcion(request.descripcion()));
        rutina.setDiaSemana(request.diaSemana());

        List<RutinaEjercicio> asociaciones = construirAsociaciones(
                request.ejercicios(),
                ejerciciosPorId);
        rutina.reemplazarRutinaEjercicios(asociaciones);

        rutinaRepository.flush();

        return toDetalleDto(rutina);
    }

    private void validarNombreDisponibleParaCrear(
            Long usuarioId,
            String nombre) {
        boolean nombreOcupado = rutinaRepository
                .existsByUsuario_IdAndNombreIgnoreCaseAndStatusTrue(
                        usuarioId,
                        nombre);

        if (nombreOcupado) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Ya existe una rutina activa con ese nombre.");
        }
    }

    private void validarNombreDisponibleParaActualizar(
            Long usuarioId,
            String nombre,
            Long rutinaId) {
        boolean nombreOcupado = rutinaRepository
                .existsByUsuario_IdAndNombreIgnoreCaseAndStatusTrueAndIdNot(
                        usuarioId,
                        nombre,
                        rutinaId);

        if (nombreOcupado) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Ya existe una rutina activa con ese nombre.");
        }
    }


    @Transactional
    public void eliminarRutina(Long rutinaId, Long usuarioId) {
        Rutina rutina = rutinaRepository
                .findByIdAndUsuario_Id(rutinaId, usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Rutina no encontrada."));

        if (Boolean.FALSE.equals(rutina.getStatus())) {
            return;
        }

        rutina.setStatus(false);
    }

    /**
     * Construye las asociaciones de una rutina a partir de la lista original del
     * request. El mismo ejercicio puede repetirse en bloques distintos y el
     * resultado queda ordenado por {@code orden}.
     */
    private List<RutinaEjercicio> construirAsociaciones(
            List<RutinaEjercicioCrearDto> configuraciones,
            Map<Long, Ejercicio> ejerciciosPorId) {
        return configuraciones
                .stream()
                .sorted(Comparator.comparing(RutinaEjercicioCrearDto::orden))
                .map(configuracion -> construirRutinaEjercicio(
                        configuracion,
                        ejerciciosPorId.get(configuracion.ejercicioId())))
                .toList();
    }

    private RutinaEjercicio construirRutinaEjercicio(
            RutinaEjercicioCrearDto configuracion,
            Ejercicio ejercicio) {
        validarNumerosSerie(configuracion);

        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio(
                ejercicio,
                configuracion.orden());

        configuracion.series()
                .stream()
                .sorted(Comparator.comparing(serie -> serie.numeroSerie()))
                .map(serie -> new RutinaSerie(
                        serie.numeroSerie(),
                        serie.repeticionesObjetivo(),
                        serie.pesoObjetivo()))
                .forEach(rutinaEjercicio::agregarSerie);

        return rutinaEjercicio;
    }

    private void validarNumerosSerie(
            RutinaEjercicioCrearDto configuracion) {
        Set<Integer> numerosEncontrados = new HashSet<>();
        List<Integer> numerosRepetidos = configuracion.series()
                .stream()
                .map(serie -> serie.numeroSerie())
                .filter(numero -> !numerosEncontrados.add(numero))
                .distinct()
                .toList();

        if (!numerosRepetidos.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "El ejercicio con ID " + configuracion.ejercicioId()
                            + " en el orden " + configuracion.orden()
                            + " contiene números de serie repetidos: "
                            + numerosRepetidos);
        }
    }

    private Map<Long, Ejercicio> buscarEjercicios(
            List<RutinaEjercicioCrearDto> configuraciones) {
        List<Long> idsUnicos = configuraciones
                .stream()
                .map(RutinaEjercicioCrearDto::ejercicioId)
                .distinct()
                .toList();

        Map<Long, Ejercicio> ejerciciosPorId = ejercicioRepository
                .findAllById(idsUnicos)
                .stream()
                .collect(Collectors.toMap(
                        Ejercicio::getId,
                        Function.identity()));

        List<Long> idsInexistentes = idsUnicos
                .stream()
                .filter(id -> !ejerciciosPorId.containsKey(id))
                .toList();

        if (!idsInexistentes.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "No existen los ejercicios con ID: " + idsInexistentes);
        }

        return ejerciciosPorId;
    }

    private String normalizarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }
        return descripcion.trim();
    }

    private RutinaDetalleDto toDetalleDto(Rutina rutina) {
        List<RutinaEjercicioDetalleDto> ejercicios = rutina
                .getRutinaEjercicios()
                .stream()
                .map(rutinaEjercicio -> new RutinaEjercicioDetalleDto(
                        rutinaEjercicio.getId(),
                        rutinaEjercicio.getEjercicio().getId(),
                        rutinaEjercicio.getEjercicio().getNombre(),
                        rutinaEjercicio.getEjercicio().getGrupoMuscular().getValor(),
                        rutinaEjercicio.getOrden(),
                        rutinaEjercicio.getSeries()
                                .stream()
                                .sorted(Comparator.comparing(
                                        RutinaSerie::getNumeroSerie))
                                .map(serie -> new RutinaSerieDetalleDto(
                                        serie.getId(),
                                        serie.getNumeroSerie(),
                                        serie.getRepeticionesObjetivo(),
                                        serie.getPesoObjetivo()))
                                .toList()))
                .toList();

        return new RutinaDetalleDto(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                rutina.getDiaSemana(),
                ejercicios);
    }
}
