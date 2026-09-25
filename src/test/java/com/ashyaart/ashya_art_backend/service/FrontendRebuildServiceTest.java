package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

class FrontendRebuildServiceTest {

    private HttpServer hook;
    private final AtomicInteger llamadas = new AtomicInteger();

    @BeforeEach
    void arrancarHookFalso() throws Exception {
        hook = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        hook.createContext("/hook", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) llamadas.incrementAndGet();
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        hook.start();
    }

    @AfterEach
    void pararHookFalso() {
        hook.stop(0);
    }

    private String urlHook() {
        return "http://localhost:" + hook.getAddress().getPort() + "/hook";
    }

    @Test
    void agrupaPeticionesSeguidasEnUnSoloRebuild() throws Exception {
        FrontendRebuildService service = new FrontendRebuildService(urlHook(), 1);

        service.solicitarRebuild("uno");
        service.solicitarRebuild("dos");
        service.solicitarRebuild("tres");
        Thread.sleep(2500);

        assertEquals(1, llamadas.get());
    }

    @Test
    void sinUrlConfiguradaNoLlamaANada() throws Exception {
        FrontendRebuildService service = new FrontendRebuildService("", 0);

        service.solicitarRebuild("cambio");
        Thread.sleep(500);

        assertEquals(0, llamadas.get());
    }
}
