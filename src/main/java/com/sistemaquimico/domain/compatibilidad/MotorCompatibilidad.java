
package com.sistemaquimico.domain.compatibilidad;

import java.util.Objects;
import java.util.UUID;

public final class MotorCompatibilidad {

    private final CatalogoReglasCompatibilidad catalogo;

    public MotorCompatibilidad(CatalogoReglasCompatibilidad catalogo) {
        this.catalogo = Objects.requireNonNull(
                catalogo,
                "El catálogo de reglas es obligatorio"
        );
    }

    public EvaluacionCruceCompatibilidad evaluarCruce(
            UUID idPerfilFila,
            UUID idPerfilColumna
    ) {
        Objects.requireNonNull(
                idPerfilFila,
                "El perfil de fila es obligatorio"
        );
        Objects.requireNonNull(
                idPerfilColumna,
                "El perfil de columna es obligatorio"
        );

        ClaveReglaCompatibilidad clave =
                new ClaveReglaCompatibilidad(
                        catalogo.idFuente(),
                        idPerfilFila,
                        idPerfilColumna
                );

        return new EvaluacionCruceCompatibilidad(
                clave,
                catalogo.buscar(idPerfilFila, idPerfilColumna)
        );
    }
}
