
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.Optional;

public record EvaluacionCruceCompatibilidad(
        ClaveReglaCompatibilidad clave,
        Optional<ReglaCompatibilidad> regla
) {
    public EvaluacionCruceCompatibilidad {
        Objects.requireNonNull(clave, "La clave del cruce es obligatoria");
        Objects.requireNonNull(regla, "El resultado de la consulta es obligatorio");

        regla.ifPresent(reglaEncontrada -> {
            if (!reglaEncontrada.clave().equals(clave)) {
                throw new IllegalArgumentException(
                        "La regla encontrada no corresponde a la clave evaluada"
                );
            }
        });
    }

    public boolean tieneRegla() {
        return regla.isPresent();
    }
}
