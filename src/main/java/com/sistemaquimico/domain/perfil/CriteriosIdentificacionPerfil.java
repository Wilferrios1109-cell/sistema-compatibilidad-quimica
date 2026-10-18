
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.UUID;

public record CriteriosIdentificacionPerfil(
        UUID idPerfil,
        CriterioConjunto<String> numerosOnu,
        CriterioConjunto<String> divisionesOnu,
        CriterioConjunto<CodigoPictogramaSGA> pictogramasRequeridos,
        EstadoReglaPerfil estadoRegla
) {

    public CriteriosIdentificacionPerfil {
        Objects.requireNonNull(
                idPerfil,
                "El perfil asociado es obligatorio"
        );
        Objects.requireNonNull(
                numerosOnu,
                "El criterio de números ONU es obligatorio"
        );
        Objects.requireNonNull(
                divisionesOnu,
                "El criterio de divisiones ONU es obligatorio"
        );
        Objects.requireNonNull(
                pictogramasRequeridos,
                "El criterio de pictogramas es obligatorio"
        );
        Objects.requireNonNull(
                estadoRegla,
                "El estado de la regla es obligatorio"
        );
    }
}
