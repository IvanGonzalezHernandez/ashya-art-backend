package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import com.ashyaart.ashya_art_backend.entity.CursoCompra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CursoCompraDao extends JpaRepository<CursoCompra, Long> {

	@Query("SELECT cc FROM CursoCompra cc " +
		       "JOIN FETCH cc.cliente cli " +
		       "JOIN FETCH cc.cursoFecha cf " +
		       "JOIN FETCH cf.curso c " +
		       "WHERE cc.estado = true " +
		       "AND (:cliente IS NULL OR LOWER(cli.nombre) LIKE LOWER(CONCAT('%', :cliente, '%'))) " +
		       "ORDER BY cc.id DESC")
		List<CursoCompra> findByFiltros(@Param("cliente") String cliente);


    boolean existsById(Long id);

    // Borrado logico: la fila se conserva para historial, pero deja de listarse/contar.
    @Modifying
    @Transactional
    @Query("UPDATE CursoCompra cc SET cc.estado = false WHERE cc.id = :id AND cc.estado = true")
    int borradoLogico(@Param("id") Long id);

}
