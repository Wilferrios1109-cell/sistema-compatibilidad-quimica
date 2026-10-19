
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FuenteCompatibilidadTest {

    @Test
    void debeCrearUnaFuenteValida() {
        FuenteCompatibilidad fuente = new FuenteCompatibilidad(
                UUID.randomUUID(),
                "Matriz ARL SURA",
                "Version inicial",
                TipoMatrizCompatibilidad.ALMACENAMIENTO,
                "ARL SURA",
                true
        );

        assertEquals("Matriz ARL SURA", fuente.nombre());
        assertEquals(
                TipoMatrizCompatibilidad.ALMACENAMIENTO,
                fuente.tipo()
        );
        assertTrue(fuente.activa());
    }

    @Test
    void debeRechazarUnNombreVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                new FuenteCompatibilidad(
                        UUID.randomUUID(),
                        "   ",
                        "Version inicial",
                        TipoMatrizCompatibilidad.ALMACENAMIENTO,
                        "ARL SURA",
                        true
                )
        );
    }

    @Test
    void debeRechazarUnaFuenteSinTipo() {
        assertThrows(NullPointerException.class, () ->
                new FuenteCompatibilidad(
                        UUID.randomUUID(),
                        "Matriz propia",
                        "1.0",
                        null,
                        "Empresa cliente",
                        true
                )
        );
    }
}
