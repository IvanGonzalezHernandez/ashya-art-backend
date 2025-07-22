package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Cliente;

public interface ClienteDao extends JpaRepository<Cliente, Long> {
	
	@Query("SELECT cli FROM Cliente cli " +
		       "WHERE (:nombre IS NULL OR LOWER(cli.nombre) LIKE LOWER(CONCAT(:nombre, '%'))) " +
		       "AND cli.fechaBaja IS NULL")
		List<Cliente> findByFiltros(@Param("nombre") String nombre);

    
	boolean existsById(Long id);
	
	@Modifying
	@Transactional
	@Query("UPDATE Cliente cli SET cli.fechaBaja = CURRENT_DATE WHERE cli.id = :id")
	int borradoLogico(@Param("id") Long id);


}
