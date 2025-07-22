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
		       "WHERE (:cliente IS NULL OR LOWER(cli.nombre) LIKE LOWER(CONCAT('%', :cliente, '%')))")
		List<CursoCompra> findByFiltros(@Param("cliente") String cliente);


    boolean existsById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM CursoCompra cc WHERE cc.id = :id")
    int borradoLogico(@Param("id") Long id); // AÑADIR COLUMNA Y CAMBIAR A UPDATE

}
