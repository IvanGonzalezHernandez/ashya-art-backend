package com.ashyaart.ashya_art_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.entity.Newsletter;


public interface NewsletterDao extends JpaRepository<Newsletter, Long> {

    Newsletter findByEmail(String email);

    Newsletter findByEmailAndEstadoTrue(String email);

    @Query("SELECT n FROM Newsletter n " +
           "WHERE (:email IS NULL OR LOWER(n.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<Newsletter> findByFiltros(@Param("email") String email);

    @Modifying
    @Query("UPDATE Newsletter n SET n.estado = false, n.fechaBaja = CURRENT_DATE WHERE n.id = :id")
    Integer borradoLogico(@Param("id") Long id);
}

