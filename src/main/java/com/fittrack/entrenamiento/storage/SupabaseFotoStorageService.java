package com.fittrack.entrenamiento.storage;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fittrack.shared.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

/** Implementa el almacenamiento de fotografías mediante Supabase Storage. */
@Slf4j
@Service
public class SupabaseFotoStorageService implements FotoStorageService {

    private final SupabaseStorageProperties properties;
    private final RestClient restClient;

    /**
     * Crea el cliente de Supabase Storage.
     *
     * @param properties propiedades externas de Storage
     */
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
            @SuppressWarnings("unchecked")
            Map<String, Object> respuesta = restClient.post()
                    .uri(urlFirma(ruta))
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
            if (!(urlFirmada instanceof String valor)
                    || valor.isBlank()) {
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

    private void agregarAutorizacion(HttpHeaders headers) {
        headers.set("apikey", properties.serviceKey());
        if (!properties.serviceKey().startsWith("sb_secret_")) {
            headers.setBearerAuth(properties.serviceKey());
        }
    }

    private String urlObjeto(String ruta) {
        return UriComponentsBuilder.fromUriString(urlColeccion())
                .pathSegment(ruta.split("/"))
                .build()
                .encode()
                .toUriString();
    }

    private String urlFirma(String ruta) {
        return UriComponentsBuilder
                .fromUriString(normalizarUrlBase())
                .pathSegment("storage", "v1", "object", "sign",
                        properties.bucket())
                .pathSegment(ruta.split("/"))
                .build()
                .encode()
                .toUriString();
    }

    private String urlColeccion() {
        return UriComponentsBuilder
                .fromUriString(normalizarUrlBase())
                .pathSegment("storage", "v1", "object", properties.bucket())
                .build()
                .encode()
                .toUriString();
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
