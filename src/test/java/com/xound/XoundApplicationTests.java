package com.xound;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que el contexto de Spring se cargue correctamente:
 *  - Todos los @Service / @Repository / @RestController estan bien cableados
 *  - El @RestControllerAdvice (GlobalExceptionHandler) registra sin conflictos
 *  - Los DTOs y la cadena de validacion estan disponibles
 */
@SpringBootTest
class XoundApplicationTests {

    @Test
    void contextLoads() {
        // Si el contexto carga sin lanzar excepcion, la inyeccion de dependencias
        // de toda la aplicacion (controllers, services, repositories, security,
        // exception handler) esta correcta.
    }
}
