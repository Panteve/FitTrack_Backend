package com.fittrack.entrenamiento.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/** Configuración externa para el bucket privado de Supabase Storage. */
@ConfigurationProperties(prefix = "supabase.storage")
public record SupabaseStorageProperties(
        String url,
        String serviceKey,
        String bucket,
        long signedUrlSeconds) {

    /**
     * Indica si existen todos los valores necesarios para usar Storage.
     *
     * @return {@code true} cuando la integración está configurada
     */
    public boolean estaConfigurado() {
        return StringUtils.hasText(url)
                && StringUtils.hasText(serviceKey)
                && StringUtils.hasText(bucket)
                && signedUrlSeconds > 0;
    }
}
