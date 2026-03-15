package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;

public interface TarjetaRegaloDao extends JpaRepository<TarjetaRegalo, Long> {
	
    @Query("SELECT t FROM TarjetaRegalo t " +
            "WHERE (:nombre IS NULL OR LOWER(t.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) order by t.precio desc")
     List<TarjetaRegalo> findByFiltros(
         @Param("nombre") String nombre
     );
    
    @Query("SELECT t FROM TarjetaRegalo t where t.estado = true order by t.precio desc")
    List<TarjetaRegalo> findTarjetasHabilitadas();
    
	boolean existsById(Long id);
	
    @Modifying
    @Transactional
    @Query("UPDATE TarjetaRegalo t SET t.estado = false WHERE t.id = :id")
    int borradoLogico(@Param("id") Long id);

}
