
package com.sistemaquimico.domain.compatibilidad;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoReglasCompatibilidadTest {

    private ReglaCompatibilidad crearRegla(
            UUID fuente,
            UUID perfilFila,
            UUID perfilColumna,
            ResultadoCompatibilidadMatriz resultado
    ) {
        return new ReglaCompatibilidad(
                UUID.randomUUID(),
                fuente,
                perfilFila,
                perfilColumna,
                resultado,
                Optional.empty()
        );
    }

    @Test
    void debeBuscarUnaReglaExistente() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ReglaCompatibilidad regla = crearRegla(
                fuente,
                perfilFila,
                perfilColumna,
                ResultadoCompatibilidadMatriz.VERDE
        );

        CatalogoReglasCompatibilidad catalogo =
                new CatalogoReglasCompatibilidad(
                        fuente, List.of(regla)
                );

        assertEquals(
                regla,
                catalogo.buscar(perfilFila, perfilColumna).orElseThrow()
        );
    }

    @Test
    void debeDevolverVacioCuandoNoExisteLaRegla() {
        CatalogoReglasCompatibilidad catalogo =
                new CatalogoReglasCompatibilidad(
                        UUID.randomUUID(), List.of()
                );

        assertTrue(
                catalogo.buscar(
                        UUID.randomUUID(), UUID.randomUUID()
                ).isEmpty()
        );
    }

    @Test
    void debeRechazarReglasDuplicadas() {
        UUID fuente = UUID.randomUUID();
        UUID perfilFila = UUID.randomUUID();
        UUID perfilColumna = UUID.randomUUID();

        ReglaCompatibilidad primera = crearRegla(
                fuente, perfilFila, perfilColumna,
                ResultadoCompatibilidadMatriz.VERDE
        );

        ReglaCompatibilidad segunda = crearRegla(
                fuente, perfilFila, perfilColumna,
                ResultadoCompatibilidadMatriz.ROJO
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CatalogoReglasCompatibilidad(
                        fuente, List.of(primera, segunda)
                )
        );
    }

    @Test
    void debeRechazarReglasDeOtraFuente() {
        UUID fuenteCatalogo = UUID.randomUUID();

        ReglaCompatibilidad regla = crearRegla(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                ResultadoCompatibilidadMatriz.VERDE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new CatalogoReglasCompatibilidad(
                        fuenteCatalogo, List.of(regla)
                )
        );
    }
}
