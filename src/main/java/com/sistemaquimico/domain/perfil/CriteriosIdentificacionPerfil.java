package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record CriteriosIdentificacionPerfil(
        UUID idPerfil,
        Optional<Set<String>> numerosOnu,
        Optional<Set<String>> divisionesOnu,
        Optional<Set<CodigoPictogramaSGA>> pictogramasRequeridos,
        EstadoReglaPerfil estadoRegla
) {
    public CriteriosIdentificacionPerfil {
        Objects.requireNonNull(idPerfil, "El perfil asociado es obligatorio");
        Objects.requireNonNull(numerosOnu, "Los números ONU son obligatorios como campo");
        Objects.requireNonNull(divisionesOnu, "Las divisiones ONU son obligatorias como campo");
        Objects.requireNonNull(
                pictogramasRequeridos,
                "Los pictogramas son obligatorios como campo"
        );
        Objects.requireNonNull(estadoRegla, "El estado de la regla es obligatorio");

        numerosOnu = numerosOnu.map(Set::copyOf);
        divisionesOnu = divisionesOnu.map(Set::copyOf);
        pictogramasRequeridos = pictogramasRequeridos.map(Set::copyOf);
    }
}

