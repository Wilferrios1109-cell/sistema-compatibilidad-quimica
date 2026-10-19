
package com.sistemaquimico.domain.perfil;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DatosRelevantesFDSTest {

    @Test
    void debeAceptarClasificacionConocida() {
        DatosRelevantesFDS datos = new DatosRelevantesFDS(
                Optional.of(3),
                Optional.of("2.1"),
                Optional.of("1203"),
                Optional.of(Set.of(CodigoPictogramaSGA.GHS02))
        );

        assertEquals(Optional.of(3), datos.claseOnu());
        assertEquals(Optional.of("2.1"), datos.divisionPrincipalOnu());
        assertEquals(Optional.of("1203"), datos.numeroOnu());
        assertTrue(datos.pictogramasSGA().orElseThrow()
                .contains(CodigoPictogramaSGA.GHS02));
    }

    @Test
    void debePermitirDatosQueAunSeDesconocen() {
        DatosRelevantesFDS datos = new DatosRelevantesFDS(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        assertTrue(datos.claseOnu().isEmpty());
        assertTrue(datos.divisionPrincipalOnu().isEmpty());
        assertTrue(datos.numeroOnu().isEmpty());
        assertTrue(datos.pictogramasSGA().isEmpty());
    }

    @Test
    void noDebeAceptarClaseOnuFueraDelRango() {
        assertThrows(IllegalArgumentException.class, () ->
                new DatosRelevantesFDS(
                        Optional.of(10),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    @Test
    void debeTratarUnaDivisionEnBlancoComoDesconocida() {
        DatosRelevantesFDS datos = new DatosRelevantesFDS(
                Optional.of(2),
                Optional.of("   "),
                Optional.empty(),
                Optional.empty()
        );

        assertTrue(datos.divisionPrincipalOnu().isEmpty());
    }
}
