
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClaveReglaCompatibilidadTest {

    @Test
    void debeReconocerDosClavesIguales() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ClaveReglaCompatibilidad primera =
                new ClaveReglaCompatibilidad(
                        fuente, perfilFila, perfilColumna
                );

        ClaveReglaCompatibilidad segunda =
                new ClaveReglaCompatibilidad(
                        fuente, perfilFila, perfilColumna
                );

        assertEquals(primera, segunda);
    }

    @Test
    void debeDistinguirReglasDeFuentesDiferentes() {
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ClaveReglaCompatibilidad primera =
                new ClaveReglaCompatibilidad(
                        UUID.randomUUID(), perfilFila, perfilColumna
                );

        ClaveReglaCompatibilidad segunda =
                new ClaveReglaCompatibilidad(
                        UUID.randomUUID(), perfilFila, perfilColumna
                );

        assertNotEquals(primera, segunda);
    }

    @Test
    void debeDistinguirPerfilesDeFilaDiferentes() {
        UUID fuente = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ClaveReglaCompatibilidad primera =
                new ClaveReglaCompatibilidad(
                        fuente, UUID.randomUUID(), perfilColumna
                );

        ClaveReglaCompatibilidad segunda =
                new ClaveReglaCompatibilidad(
                        fuente, UUID.randomUUID(), perfilColumna
                );

        assertNotEquals(primera, segunda);
    }

    @Test
    void debeDistinguirPerfilesDeColumnaDiferentes() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();

        ClaveReglaCompatibilidad primera =
                new ClaveReglaCompatibilidad(
                        fuente, perfilFila, UUID.randomUUID()
                );

        ClaveReglaCompatibilidad segunda =
                new ClaveReglaCompatibilidad(
                        fuente, perfilFila, UUID.randomUUID()
                );

        assertNotEquals(primera, segunda);
    }

    @Test
    void debeRechazarUnaFuenteNula() {
        assertThrows(NullPointerException.class, () ->
                new ClaveReglaCompatibilidad(
                        null,
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );
    }
}
