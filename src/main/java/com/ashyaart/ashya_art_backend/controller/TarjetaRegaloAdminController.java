package com.ashyaart.ashya_art_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloAdminCreacionDto;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraDto;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.service.EmailService;
import com.ashyaart.ashya_art_backend.service.TarjetaRegaloCompraService;

@RestController
@RequestMapping("/api/admin/tarjetas-regalo-compra")
public class TarjetaRegaloAdminController {

  @Autowired
  private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

  @Autowired
  private TarjetaRegaloCompraService tarjetaRegaloCompraService;

  @Autowired
  private EmailService emailService;

  @PostMapping
  public ResponseEntity<TarjetaRegaloCompraDto> crearManual(@RequestBody TarjetaRegaloAdminCreacionDto dto) {
    TarjetaRegaloCompraDto creada = tarjetaRegaloCompraService.crearManual(dto);
    return ResponseEntity.ok(creada);
  }

  @GetMapping("/{id}/pdf")
  public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) throws Exception {
    TarjetaRegaloCompra tarjetaCompra = tarjetaRegaloCompraDao.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarjeta regalo no encontrada"));

    byte[] pdf = emailService.generarPdfTarjetaRegalo(tarjetaCompra);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"GiftCard_" + tarjetaCompra.getCodigo() + ".pdf\"")
        .body(pdf);
  }
}
