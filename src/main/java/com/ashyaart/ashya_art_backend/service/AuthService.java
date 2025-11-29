package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.AuthDto;
import com.ashyaart.ashya_art_backend.entity.Administrador;
import com.ashyaart.ashya_art_backend.repository.AdministradorDao;

@Service
public class AuthService {

    @Autowired
    private AdministradorDao adminDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Administrador autenticar(AuthDto dto) {

        Administrador admin = adminDao.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return admin;
    }
}