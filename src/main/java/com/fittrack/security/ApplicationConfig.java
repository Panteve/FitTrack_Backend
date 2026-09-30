package com.fittrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fittrack.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor 
public class ApplicationConfig {
    
    private final UsuarioRepository usuarioRepository;

    @Bean 
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // Proporciona el 
    // AuthenticationManager para la autenticación de usuarios.
        return config.getAuthenticationManager();
    }

    @Bean 
    public AuthenticationProvider authenticationProvider() {// Configura el proveedor de autenticación que utiliza la base de datos para validar usuarios.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean 
    public PasswordEncoder passwordEncoder() {// Proporciona un codificador de contraseñas que utiliza el algoritmo BCrypt con un factor de fuerza de 12.
        return new BCryptPasswordEncoder(12);
    }


    @Bean 
    public UserDetailsService userDetailsService() {
        return correo -> usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Correo not found: " + correo));
    }

}
