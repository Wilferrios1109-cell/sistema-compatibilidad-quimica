
package com.sistemaquimico.domain.perfil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class PerfilDivisionTest {

    @Test
    void debeCrearUnaAsociacionValida() {
        UUID idPerfil = UUID.randomUUID();
        UUID idDivision = UUID.randomUUID();

        PerfilDivision asociacion = new PerfilDivision(
                idPerfil,
                idDivision
        );

        assertEquals(idPerfil, asociacion.idPerfil());
        assertEquals(idDivision, asociacion.idDivisionOnu());
    }

    @Test
    void noDebeAceptarUnPerfilNulo() {
        UUID idDivision = UUID.randomUUID();

        assertThrows(
                NullPointerException.class,
                () -> new PerfilDivision(null, idDivision)
        );
    }

    @Test
    void noDebeAceptarUnaDivisionNula() {
        UUID idPerfil = UUID.randomUUID();

        assertThrows(
                NullPointerException.class,
                () -> new PerfilDivision(idPerfil, null)
        );
    }
}
