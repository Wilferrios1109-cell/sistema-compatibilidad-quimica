
package com.sistemaquimico.domain.perfil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ResultadoIdentificacionPerfilTest {

    private PerfilMatriz crearPerfil() {
        return new PerfilMatriz(
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
    }

    @Test
    void debeCrearUnResultadoIdentificado() {
        PerfilMatriz perfil = crearPerfil();

        ResultadoIdentificacionPerfil resultado =
                ResultadoIdentificacionPerfil.identificado(perfil);

        assertEquals(
                EstadoIdentificacionPerfil.IDENTIFICADO,
                resultado.estado()
        );

        assertTrue(resultado.perfil().isPresent());
        assertEquals(perfil, resultado.perfil().orElseThrow());
    }

    @Test
    void debeCrearUnResultadoNoDeterminado() {
        ResultadoIdentificacionPerfil resultado =
                ResultadoIdentificacionPerfil.noDeterminado();

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );

        assertFalse(resultado.perfil().isPresent());
    }

    @Test
    void noDebeAceptarUnPerfilVacioSiEstaIdentificado() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new ResultadoIdentificacionPerfil(
                        EstadoIdentificacionPerfil.IDENTIFICADO,
                        Optional.empty()
                )
        );
    }

    @Test
    void noDebeAceptarUnPerfilSiNoEstaDeterminado() {
        PerfilMatriz perfil = crearPerfil();

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResultadoIdentificacionPerfil(
                        EstadoIdentificacionPerfil.NO_DETERMINADO,
                        Optional.of(perfil)
                )
        );
    }
}
