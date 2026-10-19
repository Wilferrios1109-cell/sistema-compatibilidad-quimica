
package com.sistemaquimico.domain.compatibilidad;

import com.sistemaquimico.domain.perfil.CodigoPictogramaSGA;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record ReglaCompatibilidad(
        UUID id,
        UUID idFuente,
        UUID idPerfil,
        CodigoPictogramaSGA pictograma,
        ResultadoCompatibilidadMatriz resultado,
        Set<Integer> referenciasNotas
) {
    public ReglaCompatibilidad {
        Objects.requireNonNull(id, "El identificador de la regla es obligatorio");
        Objects.requireNonNull(idFuente, "La fuente de compatibilidad es obligatoria");
        Objects.requireNonNull(idPerfil, "El perfil ONU asociado es obligatorio");
        Objects.requireNonNull(pictograma, "El pictograma SGA es obligatorio");
        Objects.requireNonNull(resultado, "El resultado de compatibilidad es obligatorio");
        Objects.requireNonNull(referenciasNotas, "Las referencias a notas son obligatorias");

        referenciasNotas = Set.copyOf(referenciasNotas);

        if (referenciasNotas.stream().anyMatch(numero -> numero == null || numero <= 0)) {
            throw new IllegalArgumentException(
                    "Las referencias a notas deben ser números positivos"
            );
        }
    }
}
