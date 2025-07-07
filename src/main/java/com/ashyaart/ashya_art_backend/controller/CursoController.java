package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.filter.CursoFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.service.CursoService;

@CrossOrigin(origins = {
	    "http://localhost:4200",
	    "https://ashya-art.onrender.com"
})
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<CursoDto>> findByFilter(CursoFilter filter) {
        List<CursoDto> cursosDto = cursoService.findByFilter(filter);
        return ResponseEntity.ok(cursosDto);
    }
}