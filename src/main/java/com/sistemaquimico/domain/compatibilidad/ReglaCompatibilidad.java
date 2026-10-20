
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ReglaCompatibilidad(
        UUID id,
        UUID idFuente,
        UUID idPerfilFila,
        UUID idPerfilColumna,
        ResultadoCompatibilidadMatriz resultado,
        Optional<UUID> idNotaMatriz
) {

    public ReglaCompatibilidad {
        Objects.requireNonNull(
                id,
                "El identificador de la regla es obligatorio"
        );
        Objects.requireNonNull(
                idFuente,
                "La fuente de compatibilidad es obligatoria"
        );
        Objects.requireNonNull(
                idPerfilFila,
                "El perfil de fila es obligatorio"
        );
        Objects.requireNonNull(
                idPerfilColumna,
                "El perfil de columna es obligatorio"
        );
        Objects.requireNonNull(
                resultado,
                "El resultado de compatibilidad es obligatorio"
        );
        Objects.requireNonNull(
                idNotaMatriz,
                "La referencia a la nota debe indicarse"
        );

        if (resultado == ResultadoCompatibilidadMatriz.NO_DETERMINADO) {
            throw new IllegalArgumentException(
                    "NO_DETERMINADO no es un resultado válido de una celda"
            );
        }
    }

    public ClaveReglaCompatibilidad clave() {
        return new ClaveReglaCompatibilidad(
                idFuente,
                idPerfilFila,
                idPerfilColumna
        );
    }
}
