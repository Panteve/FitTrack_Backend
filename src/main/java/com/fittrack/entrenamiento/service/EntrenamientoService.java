package com.fittrack.entrenamiento.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fittrack.entrenamiento.dto.EntrenamientoActualizarDto;
import com.fittrack.entrenamiento.dto.EntrenamientoCrearDto;
import com.fittrack.entrenamiento.dto.EntrenamientoDetalleDto;
import com.fittrack.entrenamiento.dto.EntrenamientoDto;
import com.fittrack.entrenamiento.dto.EntrenamientoFotoDto;
import com.fittrack.entrenamiento.dto.RegistroSerieDetalleDto;
import com.fittrack.entrenamiento.dto.RegistroSerieRequest;
import com.fittrack.entrenamiento.entity.Entrenamiento;
import com.fittrack.entrenamiento.entity.RegistroSerie;
import com.fittrack.entrenamiento.repository.EntrenamientoRepository;
import com.fittrack.rutina.entity.Rutina;
import com.fittrack.rutina.entity.RutinaEjercicio;
import com.fittrack.rutina.repository.RutinaRepository;
import com.fittrack.shared.exception.ApiException;
import com.fittrack.shared.storage.FotoStorageService;
import com.fittrack.usuario.entity.Usuario;
import com.fittrack.usuario.repository.UsuarioRepository;

/** Contiene las reglas de negocio de los entrenamientos. */
@Service
public class EntrenamientoService {

    private static final long TAMANO_MAXIMO_FOTO = 5L * 1024L * 1024L;
    private static final Map<String, String> EXTENSIONES_PERMITIDAS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png");

    private final EntrenamientoRepository entrenamientoRepository;
    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FotoStorageService fotoStorageService;

    /**
     * Crea el servicio con todas sus dependencias.
     *
     * @param entrenamientoRepository repositorio de entrenamientos
     * @param rutinaRepository repositorio de rutinas
     * @param usuarioRepository repositorio de usuarios
     * @param fotoStorageService almacenamiento externo de fotografías
     */
    public EntrenamientoService(
            EntrenamientoRepository entrenamientoRepository,
            RutinaRepository rutinaRepository,
            UsuarioRepository usuarioRepository,
            FotoStorageService fotoStorageService) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.rutinaRepository = rutinaRepository;
        this.usuarioRepository = usuarioRepository;
        this.fotoStorageService = fotoStorageService;
    }

    /**
     * Lista los entrenamientos activos del usuario autenticado.
     *
     * @param usuarioId identificador del usuario
     * @return entrenamientos resumidos
     */
    @Transactional(readOnly = true)
    public List<EntrenamientoDto> obtenerEntrenamientos(Long usuarioId) {
        return entrenamientoRepository.findResumenByUsuarioId(usuarioId);
    }

    /**
     * Obtiene el detalle de un entrenamiento activo del usuario.
     *
     * @param entrenamientoId identificador del entrenamiento
     * @param usuarioId identificador del usuario
     * @return detalle encontrado
     */
    @Transactional(readOnly = true)
    public EntrenamientoDetalleDto obtenerPorId(
            Long entrenamientoId,
            Long usuarioId) {
        Entrenamiento entrenamiento = buscarDetalleActivo(
                entrenamientoId,
                usuarioId);
        return toDetalleDto(entrenamiento);
    }

    /**
     * Guarda un entrenamiento finalizado con sus series realizadas.
     *
     * @param request datos enviados por el frontend
     * @param usuarioId identificador del usuario autenticado
     * @return entrenamiento creado
     */
    @Transactional
    public EntrenamientoDetalleDto crear(
            EntrenamientoCrearDto request,
            Long usuarioId) {
        Rutina rutina = buscarRutinaActiva(request.rutinaId(), usuarioId);
        List<RegistroSerie> registros = construirRegistros(
                request.series(),
                rutina);
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        Entrenamiento entrenamiento = new Entrenamiento(
                usuario,
                rutina,
                request.fecha(),
                request.duracionMinutos(),
                obtenerSeriesTotales(request.seriesTotales(), registros.size()),
                normalizarNotas(request.notas()));
        entrenamiento.reemplazarRegistrosSeries(registros);

        Entrenamiento guardado = entrenamientoRepository.saveAndFlush(
                entrenamiento);
        return toDetalleDto(guardado);
    }

    /**
     * Reemplaza los datos editables y las series de un entrenamiento.
     *
     * @param entrenamientoId identificador del entrenamiento
     * @param request datos nuevos
     * @param usuarioId identificador del propietario
     * @return detalle actualizado
     */
    @Transactional
    public EntrenamientoDetalleDto actualizar(
            Long entrenamientoId,
            EntrenamientoActualizarDto request,
            Long usuarioId) {
        Entrenamiento entrenamiento = buscarDetalleActivo(
                entrenamientoId,
                usuarioId);
        Rutina rutina = buscarRutinaActiva(request.rutinaId(), usuarioId);
        List<RegistroSerie> registros = construirRegistros(
                request.series(),
                rutina);

        entrenamiento.setRutina(rutina);
        entrenamiento.setFecha(request.fecha());
        entrenamiento.setDuracionMinutos(request.duracionMinutos());
        entrenamiento.setSeriesTotales(
                obtenerSeriesTotales(request.seriesTotales(), registros.size()));
        entrenamiento.setNotas(normalizarNotas(request.notas()));
        entrenamiento.reemplazarRegistrosSeries(registros);
        entrenamientoRepository.flush();

        return toDetalleDto(entrenamiento);
    }

    /**
     * Desactiva lógicamente un entrenamiento de forma idempotente.
     *
     * @param entrenamientoId identificador del entrenamiento
     * @param usuarioId identificador del propietario
     */
    @Transactional
    public void eliminar(Long entrenamientoId, Long usuarioId) {
        Entrenamiento entrenamiento = entrenamientoRepository
                .findByIdAndUsuario_Id(entrenamientoId, usuarioId)
                .orElseThrow(this::entrenamientoNoEncontrado);
        if (Boolean.FALSE.equals(entrenamiento.getStatus())) {
            return;
        }
        entrenamiento.setStatus(false);
    }

    /**
     * Sube o reemplaza la fotografía de un entrenamiento activo.
     *
     * @param entrenamientoId identificador del entrenamiento
     * @param foto archivo recibido por multipart
     * @param usuarioId identificador del propietario
     * @return URL firmada de la fotografía nueva
     */
    @Transactional
    public EntrenamientoFotoDto subirFoto(
            Long entrenamientoId,
            MultipartFile foto,
            Long usuarioId) {
        Entrenamiento entrenamiento = entrenamientoRepository
                .findByIdAndUsuario_IdAndStatusTrue(
                        entrenamientoId,
                        usuarioId)
                .orElseThrow(this::entrenamientoNoEncontrado);
        String contentType = validarFoto(foto);
        String rutaAnterior = entrenamiento.getUrlFoto();
        String rutaNueva = construirRutaFoto(
                usuarioId,
                entrenamientoId,
                EXTENSIONES_PERMITIDAS.get(contentType));

        byte[] contenido;
        try {
            contenido = foto.getBytes();
        } catch (IOException ex) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "No fue posible leer la fotografía enviada.");
        }

        fotoStorageService.subir(rutaNueva, contenido, contentType);
        String urlFirmada;
        try {
            urlFirmada = fotoStorageService.generarUrlFirmada(rutaNueva);
        } catch (RuntimeException ex) {
            fotoStorageService.eliminar(rutaNueva);
            throw ex;
        }
        try {
            entrenamiento.setUrlFoto(rutaNueva);
            entrenamientoRepository.flush();
        } catch (RuntimeException ex) {
            fotoStorageService.eliminar(rutaNueva);
            throw ex;
        }

        if (rutaAnterior != null && !rutaAnterior.isBlank()) {
            fotoStorageService.eliminar(rutaAnterior);
        }
        return new EntrenamientoFotoDto(
                entrenamientoId,
                urlFirmada);
    }

    private Entrenamiento buscarDetalleActivo(
            Long entrenamientoId,
            Long usuarioId) {
        return entrenamientoRepository
                .findDistinctByIdAndUsuario_IdAndStatusTrue(
                        entrenamientoId,
                        usuarioId)
                .orElseThrow(this::entrenamientoNoEncontrado);
    }

    private Rutina buscarRutinaActiva(Long rutinaId, Long usuarioId) {
        return rutinaRepository
                .findDistinctByIdAndUsuario_IdAndStatusTrue(
                        rutinaId,
                        usuarioId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "La rutina indicada no existe o no está activa."));
    }

    private List<RegistroSerie> construirRegistros(
            List<RegistroSerieRequest> solicitudes,
            Rutina rutina) {
        Map<Long, RutinaEjercicio> bloquesPorId = rutina
                .getRutinaEjercicios()
                .stream()
                .collect(Collectors.toMap(
                        RutinaEjercicio::getId,
                        Function.identity()));
        validarBloques(solicitudes, bloquesPorId);
        validarSeriesDuplicadas(solicitudes);

        return solicitudes.stream()
                .sorted(Comparator
                        .comparing((RegistroSerieRequest serie) ->
                                bloquesPorId.get(serie.rutinaEjercicioId())
                                        .getOrden(),
                                Comparator.nullsLast(
                                        Comparator.naturalOrder()))
                .thenComparing(RegistroSerieRequest::numeroSerie))
                .map(serie -> {
                    RutinaEjercicio bloque = bloquesPorId.get(
                            serie.rutinaEjercicioId());
                    return new RegistroSerie(
                            bloque.getEjercicio(),
                            bloque.getId(),
                            bloque.getOrden(),
                            serie.numeroSerie(),
                            serie.repeticiones(),
                            serie.peso());
                })
                .toList();
    }

    private void validarBloques(
            List<RegistroSerieRequest> solicitudes,
            Map<Long, RutinaEjercicio> bloquesPorId) {
        List<Long> idsInvalidos = solicitudes.stream()
                .map(RegistroSerieRequest::rutinaEjercicioId)
                .distinct()
                .filter(id -> !bloquesPorId.containsKey(id))
                .toList();
        if (!idsInvalidos.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Los bloques de ejercicio no pertenecen a la rutina: "
                            + idsInvalidos);
        }
    }

    private void validarSeriesDuplicadas(
            List<RegistroSerieRequest> solicitudes) {
        Set<SerieKey> encontradas = new HashSet<>();
        List<SerieKey> duplicadas = solicitudes.stream()
                .map(serie -> new SerieKey(
                        serie.rutinaEjercicioId(),
                        serie.numeroSerie()))
                .filter(key -> !encontradas.add(key))
                .distinct()
                .toList();
        if (!duplicadas.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Hay números de serie repetidos dentro del mismo bloque: "
                            + duplicadas);
        }
    }

    private String validarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografía no puede estar vacía.");
        }
        if (foto.getSize() > TAMANO_MAXIMO_FOTO) {
            throw new ApiException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "La fotografía no puede superar 5 MB.");
        }
        String contentType = foto.getContentType();
        if (contentType == null
                || !EXTENSIONES_PERMITIDAS.containsKey(contentType)) {
            throw new ApiException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "La fotografía debe estar en formato JPEG o PNG.");
        }
        return contentType;
    }

    private String construirRutaFoto(
            Long usuarioId,
            Long entrenamientoId,
            String extension) {
        return usuarioId + "/" + entrenamientoId + "/"
                + UUID.randomUUID() + extension;
    }

    private String normalizarNotas(String notas) {
        if (notas == null || notas.isBlank()) {
            return null;
        }
        return notas.trim();
    }

    private EntrenamientoDetalleDto toDetalleDto(
            Entrenamiento entrenamiento) {
        Comparator<RegistroSerie> comparador = Comparator
                .comparing(RegistroSerie::getOrdenEjercicio,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RegistroSerie::getNumeroSerie);
        List<RegistroSerieDetalleDto> series = entrenamiento
                .getRegistrosSeries()
                .stream()
                .sorted(comparador)
                .map(this::toSerieDetalleDto)
                .toList();
        String fotoUrl = entrenamiento.getUrlFoto() == null
                ? null
                : fotoStorageService.generarUrlFirmada(
                        entrenamiento.getUrlFoto());

        return new EntrenamientoDetalleDto(
                entrenamiento.getId(),
                entrenamiento.getRutina().getId(),
                entrenamiento.getRutina().getNombre(),
                entrenamiento.getFecha(),
                entrenamiento.getDuracionMinutos(),
                obtenerSeriesTotales(
                        entrenamiento.getSeriesTotales(),
                        series.size()),
                entrenamiento.getNotas(),
                fotoUrl,
                series);
    }

    private RegistroSerieDetalleDto toSerieDetalleDto(
            RegistroSerie registro) {
        return new RegistroSerieDetalleDto(
                registro.getId(),
                registro.getRutinaEjercicioId(),
                registro.getEjercicio().getId(),
                registro.getEjercicio().getNombre(),
                registro.getEjercicio().getGrupoMuscular().getValor(),
                registro.getOrdenEjercicio(),
                registro.getNumeroSerie(),
                registro.getRepeticiones(),
                registro.getPeso());
    }

    private int obtenerSeriesTotales(
            Integer seriesTotales,
            int seriesCompletadas) {
        if (seriesTotales == null) {
            return seriesCompletadas;
        }
        if (seriesTotales < seriesCompletadas) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Las series totales no pueden ser menores que las completadas.");
        }
        return seriesTotales;
    }

    private ApiException entrenamientoNoEncontrado() {
        return new ApiException(
                HttpStatus.NOT_FOUND,
                "Entrenamiento no encontrado.");
    }

    private record SerieKey(Long rutinaEjercicioId, Integer numeroSerie) {
    }
}
