package com.fittrack.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



@Service 
public class JwtService {

    private final String SECRET_KEY = "MALKD64SA4SA465ASAASANJ"; // Clave secreta para firmar el token.

    public String getToken(UserDetails usuario) {
        return getToken(new HashMap<>(), usuario);//Hashmas es una clase de colecciones que se utiliza para almacenar pares clave-valor. En este caso, se crea un HashMap vacío que se pasa como argumento
        //  al método getToken junto con el objeto usuario.  
    }

    private String getToken(Map<String, Object> extraclaims, UserDetails usuario) { //Metodo privado que genera un token JWT (JSON Web Token) para un usuario específico. 
    // Toma dos parámetros: un mapa de reclamaciones adicionales (extraclaims) y un objeto UserDetails que representa al usuario para el cual se generará el token.
        return Jwts.builder()
                .setClaims(extraclaims)
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token válido por 1 hora
                .signWith(getKey(), SignatureAlgorithm.HS256) // Firma el token utilizando la clave secreta y el algoritmo HS256
                .compact();
    }

    private Key getKey() {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decodifica la clave secreta en bytes utilizando Base64
       return Keys.hmacShaKeyFor(keyBytes);// Crea una clave HMAC-SHA a partir de los bytes decodificados y la devuelve como objeto Key
    }
    
}
