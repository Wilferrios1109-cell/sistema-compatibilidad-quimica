
package com.sistemaquimico.domain.compatibilidad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class CatalogoReglasCompatibilidad {

    private final UUID idFuente;
    private final Map<ClaveReglaCompatibilidad, ReglaCompatibilidad> reglas;

    public CatalogoReglasCompatibilidad(
            UUID idFuente,
            List<ReglaCompatibilidad> reglas
    ) {
        this.idFuente = Objects.requireNonNull(
                idFuente,
                "La fuente del catálogo es obligatoria"
        );

        Objects.requireNonNull(
                reglas,
                "La lista de reglas es obligatoria"
        );

        Map<ClaveReglaCompatibilidad, ReglaCompatibilidad> mapa =
                new HashMap<>();

        for (ReglaCompatibilidad regla : reglas) {
            Objects.requireNonNull(
                    regla,
                    "El catálogo no puede contener reglas nulas"
            );

            if (!this.idFuente.equals(regla.idFuente())) {
                throw new IllegalArgumentException(
                        "Todas las reglas deben pertenecer a la fuente del catálogo"
                );
            }

            ReglaCompatibilidad anterior = mapa.putIfAbsent(
                    regla.clave(),
                    regla
            );

            if (anterior != null) {
                throw new IllegalArgumentException(
                        "Existe una regla duplicada para la misma fuente, "
                                + "perfil de fila y perfil de columna"
                );
            }
        }

        this.reglas = Map.copyOf(mapa);
    }

    public UUID idFuente() {
        return idFuente;
    }

    public int cantidadReglas() {
        return reglas.size();
    }

    public Optional<ReglaCompatibilidad> buscar(
            UUID idPerfilFila,
            UUID idPerfilColumna
    ) {
        ClaveReglaCompatibilidad clave =
                new ClaveReglaCompatibilidad(
                        idFuente,
                        idPerfilFila,
                        idPerfilColumna
                );

        return Optional.ofNullable(reglas.get(clave));
    }
}
