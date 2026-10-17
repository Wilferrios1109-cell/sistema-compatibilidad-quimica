package com.sistemaquimico.domain.perfil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PerfilMatcher {

    public ResultadoIdentificacionPerfil identificar(
            TipoPerfil tipoBuscado,
            DatosRelevantesFDS datosFDS,
            List<PerfilMatriz> perfiles,
            List<CriteriosIdentificacionPerfil> criterios) {

        Objects.requireNonNull(tipoBuscado, "El tipo de perfil es obligatorio");
        Objects.requireNonNull(datosFDS, "Los datos de la FDS son obligatorios");
        Objects.requireNonNull(perfiles, "Los perfiles son obligatorios");
        Objects.requireNonNull(criterios, "Los criterios son obligatorios");

        // Sin la clase ONU no podemos restringir los candidatos.
        if (datosFDS.claseOnu().isEmpty()) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        Map<UUID, CriteriosIdentificacionPerfil> criteriosPorPerfil =
                obtenerCriteriosPorPerfil(criterios);

        List<PerfilMatriz> candidatos = new ArrayList<>();
        boolean hayReglaPendienteRelevante = false;

        for (PerfilMatriz perfil : perfiles) {

            if (perfil.tipoPerfil() != tipoBuscado
                    || perfil.claseOnu() != datosFDS.claseOnu().get()) {
                continue;
            }

            CriteriosIdentificacionPerfil criterio =
                    criteriosPorPerfil.get(perfil.id());

            // Sin criterios registrados, el perfil no se identifica.
            if (criterio == null) {
                hayReglaPendienteRelevante = true;
                continue;
            }

            // No se automatiza la identificación con reglas pendientes.
            if (criterio.estadoRegla() != EstadoReglaPerfil.VALIDADO
                    || perfil.estado() != EstadoPerfil.VALIDADO
                    || perfil.modoDivision() == ModoDivision.PENDIENTE) {
                hayReglaPendienteRelevante = true;
                continue;
            }

            if (coincideConCriterios(perfil, criterio, datosFDS)) {
                candidatos.add(perfil);
            }
        }

        // Una regla pendiente de la misma clase puede ocultar un candidato
        // alternativo. En ese caso no elegimos un perfil automáticamente.
        if (hayReglaPendienteRelevante) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        if (candidatos.isEmpty()) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        List<PerfilMatriz> especificos = candidatos.stream()
                .filter(p -> p.modoDivision() == ModoDivision.LISTA_ESPECIFICA)
                .toList();

        List<PerfilMatriz> generales = candidatos.stream()
                .filter(p -> p.modoDivision() == ModoDivision.GENERAL)
                .toList();

        // Un perfil específico puede prevalecer sobre uno general,
        // siempre que exista un único candidato específico.
        if (especificos.size() == 1) {
            return ResultadoIdentificacionPerfil.identificado(especificos.get(0));
        }

        // Varios perfiles específicos representan una ambigüedad.
        if (especificos.size() > 1) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        if (generales.size() == 1) {
            return ResultadoIdentificacionPerfil.identificado(generales.get(0));
        }

        return ResultadoIdentificacionPerfil.noDeterminado();
    }

    private boolean coincideConCriterios(
            PerfilMatriz perfil,
            CriteriosIdentificacionPerfil criterio,
            DatosRelevantesFDS datosFDS) {

        // Si el perfil declara un número ONU concreto, debe coincidir.
        if (perfil.numeroOnu() != null) {
            if (datosFDS.numeroOnu().isEmpty()
                    || !perfil.numeroOnu().equals(datosFDS.numeroOnu().get())) {
                return false;
            }
        }

        return coincideNumeroOnu(criterio.numerosOnu(), datosFDS.numeroOnu())
                && coincideDivisiones(criterio.divisionesOnu(), datosFDS.divisionesOnu())
                && coincidePictogramas(
                        criterio.pictogramasRequeridos(),
                        datosFDS.pictogramasSGA()
                );
    }

    private boolean coincideNumeroOnu(
            Optional<Set<String>> numerosPermitidos,
            Optional<String> numeroFDS) {

        if (numerosPermitidos.isEmpty()) {
            return true;
        }

        if (numerosPermitidos.get().isEmpty() || numeroFDS.isEmpty()) {
            return false;
        }

        return numerosPermitidos.get().contains(numeroFDS.get());
    }

    private boolean coincideDivisiones(
            Optional<Set<String>> divisionesPermitidas,
            Optional<Set<String>> divisionesFDS) {

        if (divisionesPermitidas.isEmpty()) {
            return true;
        }

        if (divisionesPermitidas.get().isEmpty() || divisionesFDS.isEmpty()) {
            return false;
        }

        // Todas las divisiones de la FDS deben estar contempladas
        // por el criterio del perfil.
        return divisionesPermitidas.get().containsAll(divisionesFDS.get());
    }

    private boolean coincidePictogramas(
            Optional<Set<CodigoPictogramaSGA>> pictogramasRequeridos,
            Optional<Set<CodigoPictogramaSGA>> pictogramasFDS) {

        if (pictogramasRequeridos.isEmpty()) {
            return true;
        }

        if (pictogramasRequeridos.get().isEmpty() || pictogramasFDS.isEmpty()) {
            return false;
        }

        // Todos los pictogramas requeridos deben estar presentes.
        return pictogramasFDS.get().containsAll(pictogramasRequeridos.get());
    }

    private Map<UUID, CriteriosIdentificacionPerfil> obtenerCriteriosPorPerfil(
            List<CriteriosIdentificacionPerfil> criterios) {

        Map<UUID, CriteriosIdentificacionPerfil> resultado = new HashMap<>();

        for (CriteriosIdentificacionPerfil criterio : criterios) {
            Objects.requireNonNull(criterio, "No puede haber criterios nulos");

            CriteriosIdentificacionPerfil anterior =
                    resultado.putIfAbsent(criterio.idPerfil(), criterio);

            if (anterior != null) {
                throw new IllegalArgumentException(
                        "Hay criterios duplicados para el perfil: "
                                + criterio.idPerfil()
                );
            }
        }

        return resultado;
    }
}