
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Set;

public record CriterioConjunto<T>(
        AplicabilidadCriterio aplicabilidad,
        Set<T> valores
) {

    public CriterioConjunto {
        Objects.requireNonNull(
                aplicabilidad,
                "La aplicabilidad del criterio es obligatoria"
        );
        Objects.requireNonNull(
                valores,
                "El conjunto de valores es obligatorio"
        );

        valores = Set.copyOf(valores);

        if (aplicabilidad == AplicabilidadCriterio.APLICA
                && valores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Un criterio aplicable debe tener valores"
            );
        }

        if (aplicabilidad == AplicabilidadCriterio.NO_APLICA
                && !valores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Un criterio no aplicable no debe tener valores"
            );
        }
    }

    public static <T> CriterioConjunto<T> aplica(Set<T> valores) {
        return new CriterioConjunto<>(
                AplicabilidadCriterio.APLICA,
                valores
        );
    }

    public static <T> CriterioConjunto<T> noAplica() {
        return new CriterioConjunto<>(
                AplicabilidadCriterio.NO_APLICA,
                Set.of()
        );
    }
}
