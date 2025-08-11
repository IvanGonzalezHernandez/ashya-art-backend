package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ashyaart.ashya_art_backend.entity.Secreto;

public interface SecretoDao extends JpaRepository<Secreto, Long> {

    @Query("SELECT s FROM Secreto s " +
           "WHERE (:nombre IS NULL OR LOWER(s.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) " +
           "AND s.estado = true")
    List<Secreto> findByFiltros(@Param("nombre") String nombre);

}
