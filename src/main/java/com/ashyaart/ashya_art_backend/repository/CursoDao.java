package com.ashyaart.ashya_art_backend.repository;

import com.ashyaart.ashya_art_backend.entity.Curso;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;

@Repository
public interface CursoDao extends PagingAndSortingRepository<Curso, Long> {

    @Query("SELECT c FROM Curso c " +
            "WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) " +
            "AND (c.estado = true)")
     List<Curso> findByFiltros(
         @Param("nombre") String nombre
     );
}
