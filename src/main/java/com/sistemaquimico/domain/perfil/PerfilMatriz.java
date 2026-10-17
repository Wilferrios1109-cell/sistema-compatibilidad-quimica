
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.UUID;

public record PerfilMatriz(
        UUID id,
        UUID idFuente,
        String codigo,
        int ordinal,
        String etiqueta,
        TipoPerfil tipoPerfil,
        int claseOnu,
        String numeroOnu,
        String condicionEspecial,
        EstadoPerfil estado,
        ModoDivision modoDivision
) {

    public PerfilMatriz {
        Objects.requireNonNull(id, "El ID del perfil es obligatorio");
        Objects.requireNonNull(idFuente, "La fuente es obligatoria");
        Objects.requireNonNull(tipoPerfil, "El tipo de perfil es obligatorio");
        Objects.requireNonNull(estado, "El estado del perfil es obligatorio");
        Objects.requireNonNull(modoDivision, "El modo de división es obligatorio");

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código del perfil es obligatorio"
            );
        }

        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException(
                    "La etiqueta del perfil es obligatoria"
            );
        }

        if (ordinal <= 0) {
            throw new IllegalArgumentException(
                    "El ordinal debe ser mayor que cero"
            );
        }

        if (claseOnu < 1 || claseOnu > 9) {
            throw new IllegalArgumentException(
                    "La clase ONU debe estar entre 1 y 9"
            );
        }

        codigo = codigo.strip();
        etiqueta = etiqueta.strip();

        if (numeroOnu != null) {
            numeroOnu = numeroOnu.strip();

            if (numeroOnu.isEmpty()) {
                numeroOnu = null;
            }
        }

        if (condicionEspecial != null) {
            condicionEspecial = condicionEspecial.strip();

            if (condicionEspecial.isEmpty()) {
                condicionEspecial = null;
            }
        }
    }
}
