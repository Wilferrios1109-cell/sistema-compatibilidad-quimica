
package com.sistemaquimico.domain.perfil;

import java.util.Objects;
import java.util.Optional;

/**
 * Representa el resultado de identificar un perfil de la matriz.
 */
public record ResultadoIdentificacionPerfil(
        EstadoIdentificacionPerfil estado,
        Optional<PerfilMatriz> perfil
) {

    public ResultadoIdentificacionPerfil {
        Objects.requireNonNull(
                estado,
                "El estado de identificación es obligatorio"
        );

        Objects.requireNonNull(
                perfil,
                "El resultado debe indicar si contiene un perfil"
        );

        if (estado == EstadoIdentificacionPerfil.IDENTIFICADO
                && perfil.isEmpty()) {
            throw new IllegalArgumentException(
                    "Un resultado identificado debe contener un perfil"
            );
        }

        if (estado == EstadoIdentificacionPerfil.NO_DETERMINADO
                && perfil.isPresent()) {
            throw new IllegalArgumentException(
                    "Un resultado no determinado no puede contener un perfil"
            );
        }
    }

    public static ResultadoIdentificacionPerfil identificado(
            PerfilMatriz perfil) {

        return new ResultadoIdentificacionPerfil(
                EstadoIdentificacionPerfil.IDENTIFICADO,
                Optional.of(Objects.requireNonNull(
                        perfil,
                        "El perfil identificado es obligatorio"
                ))
        );
    }

    public static ResultadoIdentificacionPerfil noDeterminado() {
        return new ResultadoIdentificacionPerfil(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                Optional.empty()
        );
    }
}
