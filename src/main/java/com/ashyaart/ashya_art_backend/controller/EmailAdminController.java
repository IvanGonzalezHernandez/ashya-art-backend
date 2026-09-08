package com.ashyaart.ashya_art_backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.service.EmailService;

@RestController
@RequestMapping("/api/admin/emails")
public class EmailAdminController {

  @Autowired
  private EmailService emailService;

  @GetMapping
  public Map<String, Object> listarEmails(
      @RequestParam(defaultValue = "20") int limit,
      @RequestParam(required = false) String after,
      @RequestParam(required = false) String before
  ) {
    return emailService.listarEmailsEnviados(limit, after, before);
  }

  @GetMapping("/{id}")
  public Map<String, Object> obtenerEmail(@PathVariable String id) {
    return emailService.obtenerEmail(id);
  }
}
