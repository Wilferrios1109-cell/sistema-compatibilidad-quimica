
package com.sistemaquimico.domain.compatibilidad;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record FuenteCompatibilidad(
        UUID id,
        String nombre,
        String version,
        Optional<LocalDateTime> fechaPublicacion,
        EstadoFuenteCompatibilidad estado
) {

    public FuenteCompatibilidad {
        Objects.requireNonNull(
                id,
                "El identificador de la fuente es obligatorio"
        );
        Objects.requireNonNull(
                nombre,
                "El nombre de la fuente es obligatorio"
        );
        Objects.requireNonNull(
                version,
                "La versión de la fuente es obligatoria"
        );
        Objects.requireNonNull(
                fechaPublicacion,
                "La fecha de publicación debe indicarse"
        );
        Objects.requireNonNull(
                estado,
                "El estado de la fuente es obligatorio"
        );

        nombre = nombre.trim();
        version = version.trim();

        if (nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la fuente no puede estar vacío"
            );
        }

        if (version.isBlank()) {
            throw new IllegalArgumentException(
                    "La versión de la fuente no puede estar vacía"
            );
        }
    }
}
