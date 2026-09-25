package com.fittrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fittrack.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
