package com.ashyaart.ashya_art_backend.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class TokenService {

    public String generarToken(String email) {
        String raw = email + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }
}