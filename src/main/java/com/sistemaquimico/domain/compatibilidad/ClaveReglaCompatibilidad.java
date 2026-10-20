
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.UUID;

public record ClaveReglaCompatibilidad(
        UUID idFuente,
        UUID idPerfilFila,
        UUID idPerfilColumna
) {

    public ClaveReglaCompatibilidad {
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
    }
}
