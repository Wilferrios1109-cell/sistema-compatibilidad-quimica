
package com.sistemaquimico.domain.perfil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class PerfilMatrizTest {

    @Test
    void debeCrearUnPerfilValido() {
        PerfilMatriz perfil = new PerfilMatriz(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "F01",
                1,
                "Explosivos",
                TipoPerfil.FILA,
                1,
                null,
                null,
                EstadoPerfil.PENDIENTE,
                ModoDivision.PENDIENTE
        );

        assertNotNull(perfil);
        assertEquals("F01", perfil.codigo());
        assertEquals(TipoPerfil.FILA, perfil.tipoPerfil());
        assertEquals(EstadoPerfil.PENDIENTE, perfil.estado());
    }

    @Test
    void noDebeAceptarUnaClaseOnuInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
                new PerfilMatriz(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "F01",
                        1,
                        "Explosivos",
                        TipoPerfil.FILA,
                        10,
                        null,
                        null,
                        EstadoPerfil.PENDIENTE,
                        ModoDivision.PENDIENTE
                )
        );
    }

    @Test
    void noDebeAceptarUnCodigoVacio() {
        assertThrows(IllegalArgumentException.class, () ->
                new PerfilMatriz(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        " ",
                        1,
                        "Explosivos",
                        TipoPerfil.FILA,
                        1,
                        null,
                        null,
                        EstadoPerfil.PENDIENTE,
                        ModoDivision.PENDIENTE
                )
        );
    }

    @Test
    void debeNormalizarElCodigoYLaEtiqueta() {
        PerfilMatriz perfil = new PerfilMatriz(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " F01 ",
                1,
                " Explosivos ",
                TipoPerfil.FILA,
                1,
                null,
                null,
                EstadoPerfil.PENDIENTE,
                ModoDivision.PENDIENTE
        );

        assertEquals("F01", perfil.codigo());
        assertEquals("Explosivos", perfil.etiqueta());
    }
}
