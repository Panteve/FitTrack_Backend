package com.fittrack.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fittrack.usuario.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secretKey;

    public JwtService(@Value("${JWT_SECRET}") String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Genera un JWT con el identificador y el correo del usuario.
     *
     * @param usuario usuario para el que se genera el token
     * @return JWT firmado
     */
    public String getToken(Usuario usuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("usuarioId", usuario.getId());
        return getToken(extraClaims, usuario);
    }

    private String getToken(Map<String, Object> extraclaims, Usuario usuario) {
        return Jwts.builder()
                .setClaims(extraclaims)
                .claim("correo", usuario.getCorreo())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido por 1 hora
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firma el token utilizando la clave secreta y el
                                                              // algoritmo HS256
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getCorreoFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("correo", String.class));
    }

    public Long getUsuarioIdFromToken(String token) {
        Number usuarioId = getClaimFromToken(
                token,
                claims -> claims.get("usuarioId", Number.class));
        return usuarioId.longValue();
    }

    /**
     * Valida el JWT y crea una identidad reducida con sus claims de usuario.
     * La lectura de los claims también comprueba la firma y la expiración.
     *
     * @param token JWT recibido en la petición
     * @return identidad autenticada con ID y correo
     * @throws MalformedJwtException si faltan los claims requeridos
     */
    public UsuarioAutenticado getUsuarioAutenticadoFromToken(String token) {
        Claims claims = getAllClaims(token);
        Number usuarioId = claims.get("usuarioId", Number.class);
        String correo = claims.get("correo", String.class);

        if (usuarioId == null || correo == null || correo.isBlank()) {
            throw new MalformedJwtException("El token no contiene usuarioId y correo válidos");
        }

        return new UsuarioAutenticado(usuarioId.longValue(), correo);
    }

    private Claims getAllClaims(String token) {//obtener todos los claims de mi token
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

}
