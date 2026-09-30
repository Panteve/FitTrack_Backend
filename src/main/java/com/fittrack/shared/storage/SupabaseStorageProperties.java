package com.fittrack.shared.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "supabase.storage")
public record SupabaseStorageProperties(
        String url,
        String serviceKey,
        String bucket,
        String profileBucket,
        long signedUrlSeconds) {


    public boolean estaConfigurado() {
        return StringUtils.hasText(url)
                && StringUtils.hasText(serviceKey)
                && StringUtils.hasText(bucket)
                && signedUrlSeconds > 0;
    }


    public boolean estaConfiguradoPerfil() {
        return StringUtils.hasText(url)
                && StringUtils.hasText(serviceKey)
                && StringUtils.hasText(profileBucket)
                && signedUrlSeconds > 0;
    }
}
