package com.fittrack.shared.storage;

/** Administra las fotografías de FitTrack en almacenamiento externo. */
public interface FotoStorageService {


    void subir(String ruta, byte[] contenido, String contentType);// Sube una fotografía a almacenamiento externo.

    String generarUrlFirmada(String ruta);

    String obtenerUrlFotoPerfil(String ruta);

    String guardarFotoPerfil(String ruta, byte[] contenido, String contentType);

    void eliminarFotoPerfil(String ruta);


    void eliminar(String ruta);
}
