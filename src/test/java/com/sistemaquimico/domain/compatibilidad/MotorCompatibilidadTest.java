
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MotorCompatibilidadTest {

    @Test
    void debeEvaluarUnCruceExistente() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                fuente,
                perfilFila,
                perfilColumna,
                ResultadoCompatibilidadMatriz.VERDE,
                Optional.empty()
        );

        MotorCompatibilidad motor = new MotorCompatibilidad(
                new CatalogoReglasCompatibilidad(
                        fuente, List.of(regla)
                )
        );

        EvaluacionCruceCompatibilidad evaluacion =
                motor.evaluarCruce(perfilFila, perfilColumna);

        assertTrue(evaluacion.tieneRegla());
        assertEquals(
                ResultadoCompatibilidadMatriz.VERDE,
                evaluacion.regla().orElseThrow().resultado()
        );
    }

    @Test
    void debeIndicarQueNoExisteUnaReglaParaElCruce() {
        MotorCompatibilidad motor = new MotorCompatibilidad(
                new CatalogoReglasCompatibilidad(
                        UUID.randomUUID(), List.of()
                )
        );

        EvaluacionCruceCompatibilidad evaluacion =
                motor.evaluarCruce(
                        UUID.randomUUID(), UUID.randomUUID()
                );

        assertFalse(evaluacion.tieneRegla());
        assertTrue(evaluacion.regla().isEmpty());
    }

    @Test
    void debeConservarLaNotaDeLaReglaEncontrada() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();
        UUID nota = UUID.randomUUID();

        ReglaCompatibilidad regla = new ReglaCompatibilidad(
                UUID.randomUUID(),
                fuente,
                perfilFila,
                perfilColumna,
                ResultadoCompatibilidadMatriz.AMARILLO,
                Optional.of(nota)
        );

        MotorCompatibilidad motor = new MotorCompatibilidad(
                new CatalogoReglasCompatibilidad(
                        fuente, List.of(regla)
                )
        );

        EvaluacionCruceCompatibilidad evaluacion =
                motor.evaluarCruce(perfilFila, perfilColumna);

        assertEquals(
                Optional.of(nota),
                evaluacion.regla().orElseThrow().idNotaMatriz()
        );
    }
}
