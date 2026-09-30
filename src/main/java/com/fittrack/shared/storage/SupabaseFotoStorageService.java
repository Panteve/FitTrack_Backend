package com.fittrack.shared.storage;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fittrack.shared.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

//Implementa el almacenamiento de fotografías mediante Supabase Storage.
@Slf4j
@Service
public class SupabaseFotoStorageService implements FotoStorageService {

    private final SupabaseStorageProperties properties;
    private final RestClient restClient;


    public SupabaseFotoStorageService(SupabaseStorageProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .build();
    }

    @Override
    public void subir(String ruta, byte[] contenido, String contentType) {
        validarConfiguracion();
        try {
            restClient.post()
                    .uri(urlObjeto(ruta))
                    .headers(this::agregarAutorizacion)
                    .header("x-upsert", "false")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(contenido)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.error("Supabase rechazó la subida de la fotografía. status={}",
                    ex.getStatusCode().value());
            throw errorStorage();
        } catch (RestClientException ex) {
            log.error("No fue posible conectar con Supabase Storage", ex);
            throw errorStorage();
        }
    }

    @Override
    public String generarUrlFirmada(String ruta) {
        validarConfiguracion();
        try {
            return solicitarUrlFirmada(properties.bucket(), ruta);
        } catch (RestClientResponseException ex) {
            log.error("Supabase rechazó la firma de la fotografía. status={}",
                    ex.getStatusCode().value());
            throw errorStorage();
        } catch (RestClientException ex) {
            log.error("No fue posible firmar la URL de Supabase Storage", ex);
            throw errorStorage();
        }
    }


    @Override
    public String obtenerUrlFotoPerfil(String ruta) {
        if (ruta == null || ruta.isBlank() || !properties.estaConfiguradoPerfil()) {
            return null;
        }

        try {
            return solicitarUrlFirmada(properties.profileBucket(), ruta);
        } catch (RestClientResponseException ex) {
            int codigoRespuesta = ex.getStatusCode().value();
            boolean fotoInexistente = codigoRespuesta == HttpStatus.BAD_REQUEST.value()
                    || codigoRespuesta == HttpStatus.NOT_FOUND.value();
            if (!fotoInexistente) {
                log.warn("No fue posible consultar la foto de perfil. ruta={}, status={}",
                        ruta, codigoRespuesta);
            }
            return null;
        } catch (RestClientException ex) {
            log.warn("No fue posible conectar con Supabase para consultar la foto de perfil. "
                    + "ruta={}", ruta);
            return null;
        }
    }

    @Override
    public String guardarFotoPerfil(
            String ruta,
            byte[] contenido,
            String contentType) {
        validarConfiguracionPerfil();
        try {
            restClient.post()
                    .uri(urlObjeto(properties.profileBucket(), ruta))
                    .headers(this::agregarAutorizacion)
                    .header("x-upsert", "false")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(contenido)
                    .retrieve()
                    .toBodilessEntity();
            return solicitarUrlFirmada(properties.profileBucket(), ruta);
        } catch (RestClientResponseException ex) {
            log.error("Supabase rechazó la foto de perfil. ruta={}, status={}",
                    ruta, ex.getStatusCode().value());
            eliminarFotoPerfil(ruta);
            throw errorStorage();
        } catch (RestClientException ex) {
            log.error("No fue posible guardar la foto de perfil. ruta={}",
                    ruta, ex);
            eliminarFotoPerfil(ruta);
            throw errorStorage();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void eliminarFotoPerfil(String ruta) {
        if (ruta == null || ruta.isBlank() || !properties.estaConfiguradoPerfil()) {
            return;
        }
        try {
            restClient.method(HttpMethod.DELETE)
                    .uri(urlColeccion(properties.profileBucket()))
                    .headers(this::agregarAutorizacion)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("prefixes", List.of(ruta)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            int codigoRespuesta = ex.getStatusCode().value();
            boolean fotoInexistente = codigoRespuesta == HttpStatus.BAD_REQUEST.value()
                    || codigoRespuesta == HttpStatus.NOT_FOUND.value();
            if (!fotoInexistente) {
                log.warn("Supabase rechazó eliminar una foto de perfil anterior. "
                        + "ruta={}, status={}", ruta, codigoRespuesta);
            }
        } catch (RestClientException ex) {
            log.warn("No fue posible eliminar una foto de perfil anterior. ruta={}",
                    ruta);
        }
    }

    @Override
    public void eliminar(String ruta) {
        if (!properties.estaConfigurado() || ruta == null || ruta.isBlank()) {
            return;
        }
        try {
            restClient.method(HttpMethod.DELETE)
                    .uri(urlColeccion())
                    .headers(this::agregarAutorizacion)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("prefixes", List.of(ruta)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            log.warn("No se pudo eliminar una fotografía anterior de Supabase. status={}",
                    ex.getStatusCode().value());
        } catch (RestClientException ex) {
            log.warn("No se pudo conectar con Supabase para eliminar una fotografía anterior");
        }
    }

    private void validarConfiguracion() {
        if (!properties.estaConfigurado()) {
            throw new ApiException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "El almacenamiento de fotografías no está configurado.");
        }
    }

    private void validarConfiguracionPerfil() {
        if (!properties.estaConfiguradoPerfil()) {
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "El almacenamiento de fotos de perfil no está configurado.");
        }
    }

    private void agregarAutorizacion(HttpHeaders headers) {
        headers.set("apikey", properties.serviceKey());
        if (!properties.serviceKey().startsWith("sb_secret_")) {
            headers.setBearerAuth(properties.serviceKey());
        }
    }

    private String urlObjeto(String ruta) {
        return urlObjeto(properties.bucket(), ruta);
    }

    private String urlObjeto(String bucket, String ruta) {
        return UriComponentsBuilder.fromUriString(urlColeccion(bucket))
                .pathSegment(ruta.split("/"))
                .build()
                .encode()
                .toUriString();
    }

    private String urlFirma(String bucket, String ruta) {
        return UriComponentsBuilder
                .fromUriString(normalizarUrlBase())
                .pathSegment("storage", "v1", "object", "sign",
                        bucket)
                .pathSegment(ruta.split("/"))
                .build()
                .encode()
                .toUriString();
    }

    private String urlColeccion() {
        return urlColeccion(properties.bucket());
    }

    private String urlColeccion(String bucket) {
        return UriComponentsBuilder
                .fromUriString(normalizarUrlBase())
                .pathSegment("storage", "v1", "object", bucket)
                .build()
                .encode()
                .toUriString();
    }

    @SuppressWarnings("unchecked")
    private String solicitarUrlFirmada(String bucket, String ruta) {
        Map<String, Object> respuesta = restClient.post()
                .uri(urlFirma(bucket, ruta))
                .headers(this::agregarAutorizacion)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", properties.signedUrlSeconds()))
                .retrieve()
                .body(Map.class);

        Object urlFirmada = respuesta == null
                ? null
                : respuesta.getOrDefault(
                        "signedURL",
                        respuesta.get("signedUrl"));
        if (!(urlFirmada instanceof String valor) || valor.isBlank()) {
            throw errorStorage();
        }
        if (valor.startsWith("http://") || valor.startsWith("https://")) {
            return valor;
        }
        if (valor.startsWith("/storage/v1/")) {
            return normalizarUrlBase() + valor;
        }
        if (valor.startsWith("/object/")) {
            return normalizarUrlBase() + "/storage/v1" + valor;
        }
        return normalizarUrlBase() + "/storage/v1/" + valor;
    }

    private String normalizarUrlBase() {
        return properties.url().endsWith("/")
                ? properties.url().substring(0, properties.url().length() - 1)
                : properties.url();
    }

    private ApiException errorStorage() {
        return new ApiException(
                org.springframework.http.HttpStatus.BAD_GATEWAY,
                "No fue posible completar la operación con la fotografía.");
    }
}
