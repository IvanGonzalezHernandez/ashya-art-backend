package com.ashyaart.ashya_art_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;

import jakarta.transaction.Transactional;

public interface TarjetaRegaloCompraDao extends  JpaRepository<TarjetaRegaloCompra, Long>{
	
    @Query("SELECT t FROM TarjetaRegaloCompra t WHERE t.codigo = :codigo")
    Optional<TarjetaRegaloCompra> findByCodigo(@Param("codigo") String codigo);
    
    @Modifying
    @Transactional
    @Query("UPDATE TarjetaRegaloCompra t SET t.canjeada = true, t.estado = false, t.fechaBaja = CURRENT_DATE WHERE t.codigo = :codigo")
    int marcarTarjetaRegaloComoUsada(@Param("codigo") String codigo);

}
