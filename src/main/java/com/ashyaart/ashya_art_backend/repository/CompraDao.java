package com.ashyaart.ashya_art_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ashyaart.ashya_art_backend.entity.Compra;

public interface CompraDao extends JpaRepository<Compra, Long> {
	
		Optional<Compra> findByCarritoId(String carritoId);
}
