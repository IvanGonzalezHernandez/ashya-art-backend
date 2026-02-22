package com.ashyaart.ashya_art_backend.repository;

import com.ashyaart.ashya_art_backend.entity.Curso;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CursoDao extends JpaRepository<Curso, Long> {

	@Query("SELECT c FROM Curso c " +
	        "WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) " +
	        "AND (c.estado = true) " +
	        "ORDER BY c.orden ASC")
	List<Curso> findByFiltros(
	    @Param("nombre") String nombre
	);

	boolean existsById(Long id);
	
    @Modifying
    @Transactional
    @Query("UPDATE Curso c SET c.estado = false WHERE c.id = :id")
    int borradoLogico(@Param("id") Long id);
}
