package com.ashyaart.ashya_art_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ashyaart.ashya_art_backend.entity.LogError;

public interface LogErrorDao extends JpaRepository<LogError, Long> {

}
