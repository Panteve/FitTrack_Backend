package com.fittrack.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.usuario.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByCorreo(String correo);
	boolean existsByCorreo(String correo);
	List<Usuario> findAllStatusTrue(Long usuarioId);
}
