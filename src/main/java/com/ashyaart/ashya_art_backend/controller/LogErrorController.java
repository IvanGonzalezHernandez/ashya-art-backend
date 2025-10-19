package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.filter.LogErrorFilter;
import com.ashyaart.ashya_art_backend.model.LogErrorDto;
import com.ashyaart.ashya_art_backend.service.LogErrorService;

@RestController
@RequestMapping("/api/errores")
public class LogErrorController {
	
	@Autowired
	private LogErrorService logErrorService;
	
	private static final Logger logger = LoggerFactory.getLogger(LogErrorController.class);
	
    @GetMapping
    public ResponseEntity<List<LogErrorDto>> findByFilter(LogErrorFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener logs de erores");
        List<LogErrorDto> logErrorDto = logErrorService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} errores", logErrorDto.size());
        return ResponseEntity.ok(logErrorDto);
    }

}
