package com.ashyaart.ashya_art_backend.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.ashyaart.ashya_art_backend.service.FrontendRebuildService;

class FrontendRebuildInterceptorTest {

    private final FrontendRebuildService rebuild = mock(FrontendRebuildService.class);
    private final FrontendRebuildInterceptor interceptor = new FrontendRebuildInterceptor(rebuild);

    private void ejecutar(String metodo, String ruta, int status, Exception ex) {
        MockHttpServletRequest request = new MockHttpServletRequest(metodo, ruta);
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(status);
        interceptor.afterCompletion(request, response, null, ex);
    }

    @ParameterizedTest
    @CsvSource({
        "POST, /api/productos",
        "PUT, /api/productos/3",
        "DELETE, /api/cursos/2",
        "PUT, /api/tarjetas-regalo/1",
        "PUT, /api/productos-compra/7",
        "DELETE, /api/productos-compra/7"
    })
    void escriturasCorrectasPidenRebuild(String metodo, String ruta) {
        ejecutar(metodo, ruta, 200, null);
        verify(rebuild).solicitarRebuild(anyString());
    }

    @ParameterizedTest
    @CsvSource({
        "GET, /api/productos, 200",
        "PUT, /api/productos/3, 400",
        "POST, /api/cursos/solicitud-curso, 200",
        "POST, /api/tarjetas-regalo/validar, 200",
        "POST, /api/newsletters, 200",
        "PUT, /api/cursos-fecha/2, 200"
    })
    void lecturasErroresYOtrasRutasNoPidenRebuild(String metodo, String ruta, int status) {
        ejecutar(metodo, ruta, status, null);
        verify(rebuild, never()).solicitarRebuild(anyString());
    }

    @Test
    void siHuboExcepcionNoPideRebuild() {
        ejecutar("PUT", "/api/productos/3", 200, new RuntimeException("fallo"));
        verify(rebuild, never()).solicitarRebuild(anyString());
    }
}
