package com.ashyaart.ashya_art_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ashyaart.ashya_art_backend.entity.Administrador;

public interface AdministradorDao extends JpaRepository<Administrador, Long> {
	Optional<Administrador> findByEmail(String email);
}
