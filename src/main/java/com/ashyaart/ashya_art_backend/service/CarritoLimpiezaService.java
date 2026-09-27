package com.ashyaart.ashya_art_backend.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.repository.CarritoDao;

/**
 * Borra los carritos de pago antiguos. Un carrito solo hace falta entre que se abre el pago en
 * Stripe y llega su webhook (minutos; Stripe reintenta como mucho 3 días), pero guarda los datos
 * que el cliente escribió al pagar, así que no se conservan más de lo necesario.
 * Se ejecuta cada noche y al arrancar (en Render el backend se reinicia en cada despliegue).
 */
@Service
public class CarritoLimpiezaService {

    private static final Logger logger = LoggerFactory.getLogger(CarritoLimpiezaService.class);

    private final CarritoDao carritoDao;
    private final int diasRetencion;
    private final Clock clock;

    @Autowired
    public CarritoLimpiezaService(CarritoDao carritoDao, @Value("${carrito.dias-retencion:30}") int diasRetencion) {
        this(carritoDao, diasRetencion, Clock.systemDefaultZone());
    }

    CarritoLimpiezaService(CarritoDao carritoDao, int diasRetencion, Clock clock) {
        this.carritoDao = carritoDao;
        this.diasRetencion = Math.max(diasRetencion, 7); // nunca menos de una semana: el webhook puede reintentar días
        this.clock = clock;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${carrito.limpieza-cron:0 30 3 * * *}", zone = "Europe/Berlin")
    public void limpiar() {
        try {
            // "creado" se guarda con LocalDateTime.now() del servidor, así que se compara en la misma zona
            LocalDateTime limite = LocalDateTime.now(clock).minusDays(diasRetencion);
            int borrados = carritoDao.borrarCreadosAntesDe(limite);
            if (borrados > 0) {
                logger.info("Limpieza de carritos: {} borrados (creados antes de {})", borrados, limite);
            }
        } catch (RuntimeException e) {
            logger.warn("No se pudo hacer la limpieza de carritos", e);
        }
    }
}
