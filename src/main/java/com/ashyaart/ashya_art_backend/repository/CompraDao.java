package com.ashyaart.ashya_art_backend.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ashyaart.ashya_art_backend.entity.Compra;

public interface CompraDao extends JpaRepository<Compra, Long> {
	
		Optional<Compra> findByCarritoId(String carritoId);
		
	    @Query("select coalesce(sum(c.total), 0) from Compra c where c.pagado = true")
	    BigDecimal sumTotalPagado();
}
