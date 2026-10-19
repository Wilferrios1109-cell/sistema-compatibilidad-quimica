
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotaCompatibilidadTest {

    @Test
    void debeCrearUnaNotaValida() {
        UUID idFuente = UUID.randomUUID();

        NotaCompatibilidad nota = new NotaCompatibilidad(
                UUID.randomUUID(),
                idFuente,
                1,
                "Texto de ejemplo de una nota."
        );

        assertEquals(idFuente, nota.idFuente());
        assertEquals(1, nota.numero());
        assertEquals("Texto de ejemplo de una nota.", nota.contenido());
    }

    @Test
    void debeEliminarEspaciosInnecesariosDelContenido() {
        NotaCompatibilidad nota = new NotaCompatibilidad(
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                "  Nota de ejemplo  "
        );

        assertEquals("Nota de ejemplo", nota.contenido());
    }

    @Test
    void debeRechazarUnNumeroNoPositivo() {
        assertThrows(IllegalArgumentException.class, () ->
                new NotaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        0,
                        "Nota de ejemplo"
                )
        );
    }

    @Test
    void debeRechazarUnContenidoVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                new NotaCompatibilidad(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        1,
                        "   "
                )
        );
    }
}
