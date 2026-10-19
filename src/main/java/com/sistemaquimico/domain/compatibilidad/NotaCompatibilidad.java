
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.UUID;

public record NotaCompatibilidad(
        UUID id,
        UUID idFuente,
        int numero,
        String contenido
) {
    public NotaCompatibilidad {
        Objects.requireNonNull(id, "El identificador de la nota es obligatorio");
        Objects.requireNonNull(idFuente, "La fuente de la nota es obligatoria");
        Objects.requireNonNull(contenido, "El contenido de la nota es obligatorio");

        if (numero <= 0) {
            throw new IllegalArgumentException(
                    "El número de la nota debe ser positivo"
            );
        }

        contenido = contenido.trim();

        if (contenido.isEmpty()) {
            throw new IllegalArgumentException(
                    "El contenido de la nota no puede estar vacío"
            );
        }
    }
}
