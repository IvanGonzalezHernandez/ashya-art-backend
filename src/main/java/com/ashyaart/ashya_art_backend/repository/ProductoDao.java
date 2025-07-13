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
            "WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) " +
            "AND (p.estado = true)")
     List<Producto> findByFiltros(
         @Param("nombre") String nombre
     );
    
	boolean existsById(Long id);
	
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.estado = false WHERE p.id = :id")
    int borradoLogico(@Param("id") Long id);

}
