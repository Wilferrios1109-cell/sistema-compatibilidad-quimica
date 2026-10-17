
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa la asociación entre un perfil de la matriz
 * y una división de riesgo ONU.
 */
public record PerfilDivision(
        UUID idPerfil,
        UUID idDivisionOnu
) {

    public PerfilDivision {
        Objects.requireNonNull(
                idPerfil,
                "El perfil de la matriz es obligatorio"
        );

        Objects.requireNonNull(
                idDivisionOnu,
                "La división ONU es obligatoria"
        );
    }
}
