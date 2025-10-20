package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ashyaart.ashya_art_backend.entity.SecretoCompra;

public interface SecretoCompraDao extends JpaRepository<SecretoCompra, Long> {
	
	   @Query("SELECT sc FROM SecretoCompra sc " +
		        "WHERE (:clienteNombre IS NULL OR " +
		        "     LOWER(sc.cliente.nombre) LIKE LOWER(CONCAT(:clienteNombre, '%'))) " +
		        "ORDER BY sc.fechaCompra DESC")
		    List<SecretoCompra> findByFiltros(@Param("clienteNombre") String clienteNombre);

}
