package com.ashyaart.ashya_art_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.VisitaDiaria;

public interface VisitaDiariaDao extends JpaRepository<VisitaDiaria, Long> {

    /** Suma una vista a la fila del día; la crea si no existe (clave única uk_visita_diaria). */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO visita_diaria (fecha, ruta, origen, dispositivo, idioma, visitas) "
            + "VALUES (:fecha, :ruta, :origen, :dispositivo, :idioma, 1) "
            + "ON DUPLICATE KEY UPDATE visitas = visitas + 1", nativeQuery = true)
    void sumarVisita(@Param("fecha") LocalDate fecha, @Param("ruta") String ruta, @Param("origen") String origen,
            @Param("dispositivo") String dispositivo, @Param("idioma") String idioma);

    List<VisitaDiaria> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
