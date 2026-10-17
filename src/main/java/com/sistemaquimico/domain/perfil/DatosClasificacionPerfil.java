
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Contiene los datos ONU utilizados para identificar un perfil.
 */
public record DatosClasificacionPerfil(
        TipoPerfil tipoPerfil,
        int claseOnu,
        Set<UUID> divisionesOnu
) {

    public DatosClasificacionPerfil {
        Objects.requireNonNull(
                tipoPerfil,
                "El tipo de perfil es obligatorio"
        );

        Objects.requireNonNull(
                divisionesOnu,
                "Las divisiones ONU son obligatorias"
        );

        if (claseOnu < 1 || claseOnu > 9) {
            throw new IllegalArgumentException(
                    "La clase ONU debe estar entre 1 y 9"
            );
        }

        divisionesOnu = Set.copyOf(divisionesOnu);
    }
}
