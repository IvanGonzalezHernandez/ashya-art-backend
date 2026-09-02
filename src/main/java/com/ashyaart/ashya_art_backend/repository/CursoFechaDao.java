package com.ashyaart.ashya_art_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.CursoFecha;

@Repository
public interface CursoFechaDao extends JpaRepository<CursoFecha, Long> {

    // Buscar por fecha exacta (o todos si fecha es null)

	@Query("SELECT cf FROM CursoFecha cf WHERE cf.estado = true AND (:fecha IS NULL OR cf.fecha = :fecha)")
	Stream<CursoFecha> streamByFiltros(@Param("fecha") LocalDate fecha);


    boolean existsById(Long id);

    // Borrado lógico: la fila se conserva (la referencia curso_compra.id_fecha
    // depende de ella) pero deja de aparecer en listados y disponibilidad.
    @Modifying
    @Transactional
    @Query("UPDATE CursoFecha cf SET cf.estado = false WHERE cf.id = :id")
    int borradoLogico(@Param("id") Long id);

    // Método para buscar fechas por id del curso
    @Query("SELECT cf FROM CursoFecha cf " +
    	       "WHERE cf.curso.id = :idCurso " +
    	       "AND cf.estado = true " +
    	       "AND cf.fecha >= CURRENT_DATE " +
    	       "ORDER BY cf.fecha ASC")
    List<CursoFecha> findByIdCurso(@Param("idCurso") Long idCurso);

    
    @Query("SELECT cf.plazasDisponibles FROM CursoFecha cf WHERE cf.id = :idCursoFecha")
    Integer obtenerPlazasPorIdCursoFecha(@Param("idCursoFecha") Long idCursoFecha);
}
