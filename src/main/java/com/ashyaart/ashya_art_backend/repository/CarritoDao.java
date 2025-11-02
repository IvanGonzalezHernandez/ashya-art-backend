package com.ashyaart.ashya_art_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ashyaart.ashya_art_backend.entity.Carrito;

public interface CarritoDao extends JpaRepository<Carrito, String> {
	Optional<Carrito> findByIdAndConsumidoFalse(String id);
}
