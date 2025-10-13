package com.ashyaart.ashya_art_backend.repository;

import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;

public interface TarjetaRegaloCompraDao extends JpaRepository<TarjetaRegaloCompra, Long> {

    @Query("SELECT t FROM TarjetaRegaloCompra t WHERE t.codigo = :codigo")
    Optional<TarjetaRegaloCompra> findByCodigo(@Param("codigo") String codigo);

    // Canjear por CÓDIGO 
    @Modifying
    @Transactional
    @Query("UPDATE TarjetaRegaloCompra t SET t.canjeada = true, t.estado = false, t.fechaBaja = CURRENT_DATE WHERE t.codigo = :codigo")
    int marcarTarjetaRegaloComoUsada(@Param("codigo") String codigo);

    // Canjear por ID
    @Modifying
    @Transactional
    @Query("UPDATE TarjetaRegaloCompra t SET t.canjeada = true, t.estado = false, t.fechaBaja = CURRENT_DATE WHERE t.id = :id AND t.canjeada = false")
    int canjearPorId(@Param("id") Long id);

    // Filtro simple por cliente
    @Query("SELECT t FROM TarjetaRegaloCompra t WHERE (:idCliente IS NULL OR t.cliente.id = :idCliente) AND t.fechaBaja IS NULL AND t.estado = true")
    List<TarjetaRegaloCompra> findByFilter(@Param("idCliente") Long idCliente);

    // Borrado lógico por ID
    @Modifying
    @Transactional
    @Query("UPDATE TarjetaRegaloCompra t SET t.estado = false, t.fechaBaja = CURRENT_DATE WHERE t.id = :id")
    int desactivarPorId(@Param("id") Long id);
}
