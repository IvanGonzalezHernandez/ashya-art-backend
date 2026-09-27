package com.ashyaart.ashya_art_backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Carrito;

public interface CarritoDao extends JpaRepository<Carrito, String> {
	Optional<Carrito> findByIdAndConsumidoFalse(String id);

	/** Borra los carritos creados antes de la fecha indicada (limpieza periódica). Devuelve cuántos. */
	@Modifying
	@Transactional
	@Query("DELETE FROM Carrito c WHERE c.creado < :limite")
	int borrarCreadosAntesDe(@Param("limite") LocalDateTime limite);
}
