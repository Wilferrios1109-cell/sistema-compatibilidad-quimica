
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReglaCompatibilidadTest {

    @Test
    void debeCrearUnaReglaValida() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();
        UUID nota = UUID.randomUUID();

        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                fuente,
                perfilFila,
                perfilColumna,
                ResultadoCompatibilidadMatriz.VERDE,
                Optional.of(nota)
        );

        assertEquals(fuente, regla.idFuente());
        assertEquals(perfilFila, regla.idPerfilFila());
        assertEquals(perfilColumna, regla.idPerfilColumna());
        assertEquals(ResultadoCompatibilidadMatriz.VERDE, regla.resultado());
        assertEquals(Optional.of(nota), regla.idNotaMatriz());
        assertEquals(
                new ClaveReglaCompatibilidad(
                        fuente, perfilFila, perfilColumna
                ),
                regla.clave()
        );
    }

    @Test
    void debePermitirUnaReglaSinNota() {
        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ResultadoCompatibilidadMatriz.AMARILLO,
                Optional.empty()
        );

        assertTrue(regla.idNotaMatriz().isEmpty());
    }

    @Test
    void debeRechazarUnResultadoNulo() {
        assertThrows(NullPointerException.class, () ->
                new ReglaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        Optional.empty()
                )
        );
    }

        @Test
    void debeRechazarUnaReferenciaDeNotaNula() {
        assertThrows(NullPointerException.class, () ->
                new ReglaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        ResultadoCompatibilidadMatriz.VERDE,
                        null
                )
        );
    }
}
