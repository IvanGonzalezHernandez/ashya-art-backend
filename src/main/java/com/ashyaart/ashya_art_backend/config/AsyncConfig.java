package com.ashyaart.ashya_art_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita la ejecución asíncrona en Spring.
 * Esto permite que los métodos con @Async se ejecuten en segundo plano.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
