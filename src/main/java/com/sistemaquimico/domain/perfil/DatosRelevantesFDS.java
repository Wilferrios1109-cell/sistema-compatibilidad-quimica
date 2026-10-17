package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record DatosRelevantesFDS(
        Optional<Integer> claseOnu,
        Optional<Set<String>> divisionesOnu,
        Optional<String> numeroOnu,
        Optional<Set<CodigoPictogramaSGA>> pictogramasSGA
) {

    public DatosRelevantesFDS {
        Objects.requireNonNull(claseOnu, "La clase ONU es obligatoria como campo");
        Objects.requireNonNull(divisionesOnu, "Las divisiones ONU son obligatorias como campo");
        Objects.requireNonNull(numeroOnu, "El número ONU es obligatorio como campo");
        Objects.requireNonNull(pictogramasSGA, "Los pictogramas SGA son obligatorios como campo");

        claseOnu.ifPresent(clase -> {
            if (clase < 1 || clase > 9) {
                throw new IllegalArgumentException(
                        "La clase ONU debe estar entre 1 y 9"
                );
            }
        });

        divisionesOnu = divisionesOnu.map(divisiones ->
                divisiones.stream()
                        .map(String::trim)
                        .peek(division -> {
                            if (division.isBlank()) {
                                throw new IllegalArgumentException(
                                        "Una división ONU no puede estar vacía"
                                );
                            }
                        })
                        .collect(Collectors.toUnmodifiableSet())
        );

        numeroOnu = numeroOnu
                .map(String::trim)
                .filter(numero -> !numero.isBlank());

        divisionesOnu.ifPresent(divisiones -> {
            if (divisiones.isEmpty()) {
                throw new IllegalArgumentException(
                        "Si se conocen las divisiones ONU, debe indicarse al menos una"
                );
            }
        });
    }
}