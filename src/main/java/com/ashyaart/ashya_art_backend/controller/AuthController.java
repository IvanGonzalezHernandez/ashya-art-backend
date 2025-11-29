package com.ashyaart.ashya_art_backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ashyaart.ashya_art_backend.model.AuthDto;
import com.ashyaart.ashya_art_backend.model.AuthResponse;
import com.ashyaart.ashya_art_backend.entity.Administrador;
import com.ashyaart.ashya_art_backend.service.AuthService;
import com.ashyaart.ashya_art_backend.service.TokenService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthDto dto) {
        try {
            Administrador admin = authService.autenticar(dto);
            String token = tokenService.generarToken(admin.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}