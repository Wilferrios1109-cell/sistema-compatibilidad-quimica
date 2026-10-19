
package com.sistemaquimico.domain.compatibilidad;

import com.sistemaquimico.domain.perfil.CodigoPictogramaSGA;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReglaCompatibilidadTest {

    @Test
    void debeCrearUnaReglaValida() {
        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                CodigoPictogramaSGA.GHS02,
                ResultadoCompatibilidadMatriz.VERDE,
                Set.of(1, 2)
        );

        assertEquals(CodigoPictogramaSGA.GHS02, regla.pictograma());
        assertEquals(ResultadoCompatibilidadMatriz.VERDE, regla.resultado());
        assertEquals(Set.of(1, 2), regla.referenciasNotas());
    }

    @Test
    void debeRechazarUnaReferenciaDeNotaNoPositiva() {
        assertThrows(IllegalArgumentException.class, () ->
                new ReglaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CodigoPictogramaSGA.GHS02,
                        ResultadoCompatibilidadMatriz.AMARILLO,
                        Set.of(0)
                )
        );
    }

    @Test
    void debeRechazarUnResultadoNulo() {
        assertThrows(NullPointerException.class, () ->
                new ReglaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        CodigoPictogramaSGA.GHS02,
                        null,
                        Set.of()
                )
        );
    }

    @Test
    void noDebePermitirModificarLasReferenciasDeNotas() {
        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                CodigoPictogramaSGA.GHS02,
                ResultadoCompatibilidadMatriz.VERDE,
                Set.of(1)
        );

        assertThrows(UnsupportedOperationException.class, () ->
                regla.referenciasNotas().add(2)
        );
    }
}
