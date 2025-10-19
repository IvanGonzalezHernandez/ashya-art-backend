package com.ashyaart.ashya_art_backend.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ashyaart.ashya_art_backend.entity.LogError;

public interface LogErrorDao extends JpaRepository<LogError, Long> {
	
    @Query("SELECT l FROM LogError l " +
            "WHERE (:fechaCreacion IS NULL OR l.fechaCreacion >= :fechaCreacion) " +
            "ORDER BY l.fechaCreacion DESC")
     List<LogError> findByFiltros(
         @Param("fechaCreacion") Instant fechaCreacion
     );

}
