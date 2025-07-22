package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.ProductoCompra;

public interface ProductoCompraDao extends JpaRepository<ProductoCompra, Long> {
	 @Query("SELECT pc FROM ProductoCompra pc " +
	           "WHERE (:cliente IS NULL OR LOWER(pc.cliente.nombre) LIKE LOWER(CONCAT('%', :cliente, '%')))")
	    List<ProductoCompra> findByFiltros(@Param("cliente") String cliente);

	    boolean existsById(Long id);

	    // Borrado lógico: cambiar una columna (por ejemplo 'activo' o 'fechaBaja') para marcar borrado
	    @Modifying
	    @Transactional
	    @Query("DELETE FROM ProductoCompra pc WHERE pc.id = :id")
	    int borradoLogico(@Param("id") Long id);

}
