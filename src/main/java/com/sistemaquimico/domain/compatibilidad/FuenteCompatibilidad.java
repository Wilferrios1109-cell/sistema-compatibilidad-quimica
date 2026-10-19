
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.UUID;

public record FuenteCompatibilidad(
        UUID id,
        String nombre,
        String version,
        TipoMatrizCompatibilidad tipo,
        String responsable,
        boolean activa
) {
    public FuenteCompatibilidad {
        Objects.requireNonNull(id, "El identificador es obligatorio");
        Objects.requireNonNull(tipo, "El tipo de matriz es obligatorio");

        nombre = validarTexto(nombre, "El nombre es obligatorio");
        version = validarTexto(version, "La versión es obligatoria");
        responsable = validarTexto(responsable, "El responsable es obligatorio");
    }

    private static String validarTexto(String valor, String mensaje) {
        Objects.requireNonNull(valor, mensaje);

        String texto = valor.trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }

        return texto;
    }
}
