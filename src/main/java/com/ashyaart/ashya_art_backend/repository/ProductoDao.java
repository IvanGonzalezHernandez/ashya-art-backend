package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Producto;

public interface ProductoDao extends JpaRepository<Producto, Long> {
	
    @Query("SELECT p FROM Producto p " +
            "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT(:nombre, '%')))")
     List<Producto> findByFiltros(
         @Param("nombre") String nombre
     );

    @Query("SELECT p FROM Producto p WHERE p.estado = true")
    List<Producto> findProductosHabilitados();

	boolean existsById(Long id);
	
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.estado = false WHERE p.id = :id")
    int borradoLogico(@Param("id") Long id);
    
    @Query("SELECT p.stock FROM Producto p WHERE p.id = :idProducto")
    Integer obtenerStockPorId(@Param("idProducto") Long idProducto);

    // Descuento atómico: si dos compras concurrentes piden las últimas unidades, la
    // condición "stock >= cantidad" hace que solo una de ellas afecte una fila; la otra
    // obtiene 0 y debe tratarlo como stock insuficiente (evita vender de más).
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int descontarStock(@Param("id") Long id, @Param("cantidad") Integer cantidad);

}
