package com.ashyaart.ashya_art_backend.repository;

import java.time.LocalDate;
import java.util.List;

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
    @Query("SELECT cf, c.nombre FROM CursoFecha cf " +
   		   "JOIN FETCH cf.curso c " +
           "WHERE (:fecha IS NULL OR cf.fecha = :fecha)")
    List<CursoFecha> findByFiltros(@Param("fecha") LocalDate fecha);

    boolean existsById(Long id);

    // Eliminación física (borrado real)
    @Modifying
    @Transactional
    @Query("DELETE FROM CursoFecha cf WHERE cf.id = :id")
    int borradoLogico(@Param("id") Long id);
    
    // Método para buscar fechas por id del curso
    @Query("SELECT cf FROM CursoFecha cf WHERE cf.curso.id = :idCurso")
    List<CursoFecha> findByIdCurso(@Param("idCurso") Long idCurso);
    
    @Query("SELECT cf.plazasDisponibles FROM CursoFecha cf WHERE cf.id = :idCursoFecha")
    Integer obtenerPlazasPorIdCursoFecha(@Param("idCursoFecha") Long idCursoFecha);
}
