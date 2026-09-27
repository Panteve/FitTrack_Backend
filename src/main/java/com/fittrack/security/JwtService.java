package com.fittrack.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final String secretKey;

    public JwtService(@Value("${JWT_SECRET}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String getToken(UserDetails usuario) {
        return getToken(new HashMap<>(), usuario);
    }

    private String getToken(Map<String, Object> extraclaims, UserDetails usuario) { //Método que genera un token JWT para un usuario dado.
    //  Recibe un mapa de reclamos adicionales y un objeto UserDetails que representa al usuario autenticado.
        return Jwts.builder()
                .setClaims(extraclaims)
                .setSubject(usuario.getUsername())
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
        return getClaimFromToken(token, Claims::getSubject); //Obtiene el correo electrónico del usuario a partir del token JWT.
        //getSubject() devuelve el valor del "subject" (sujeto) del token, que en este caso es el correo electrónico del usuario.
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String correo = getCorreoFromToken(token);
        return (correo.equals(userDetails.getUsername()) && !isTokenExpired(token)); //Verifica si el token es válido comparando el correo del token
        //  con el nombre de usuario del objeto UserDetails y asegurándose de que el token no haya expirado.
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

    private Date getExpiration(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

}
