package com.ashyaart.ashya_art_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita las tareas programadas (@Scheduled), como la limpieza nocturna de carritos. */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
