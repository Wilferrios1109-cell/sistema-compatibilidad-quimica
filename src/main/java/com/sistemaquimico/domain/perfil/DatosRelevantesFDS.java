
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record DatosRelevantesFDS(
        Optional<Integer> claseOnu,
        Optional<String> divisionPrincipalOnu,
        Optional<String> numeroOnu,
        Optional<Set<CodigoPictogramaSGA>> pictogramasSGA
) {

    public DatosRelevantesFDS {
        Objects.requireNonNull(
                claseOnu,
                "La clase ONU es obligatoria como campo"
        );
        Objects.requireNonNull(
                divisionPrincipalOnu,
                "La división principal ONU es obligatoria como campo"
        );
        Objects.requireNonNull(
                numeroOnu,
                "El número ONU es obligatorio como campo"
        );
        Objects.requireNonNull(
                pictogramasSGA,
                "Los pictogramas SGA son obligatorios como campo"
        );

        claseOnu.ifPresent(clase -> {
            if (clase < 1 || clase > 9) {
                throw new IllegalArgumentException(
                        "La clase ONU debe estar entre 1 y 9"
                );
            }
        });

        divisionPrincipalOnu = divisionPrincipalOnu
                .map(String::trim)
                .filter(division -> !division.isBlank());

        numeroOnu = numeroOnu
                .map(String::trim)
                .filter(numero -> !numero.isBlank());

        pictogramasSGA = pictogramasSGA.map(
                pictogramas -> pictogramas.stream()
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}
