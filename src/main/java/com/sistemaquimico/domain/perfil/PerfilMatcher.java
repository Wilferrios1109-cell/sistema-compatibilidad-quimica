
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

        if (datosFDS.claseOnu().isEmpty()) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        Map<UUID, CriteriosIdentificacionPerfil> criteriosPorPerfil =
                obtenerCriteriosPorPerfil(criterios);

        List<PerfilMatriz> candidatos = new ArrayList<>();
        boolean existeCandidatoIndeterminado = false;

        for (PerfilMatriz perfil : perfiles) {

            if (perfil.tipoPerfil() != tipoBuscado
                    || perfil.claseOnu() != datosFDS.claseOnu().get()) {
                continue;
            }

            CriteriosIdentificacionPerfil criterio =
                    criteriosPorPerfil.get(perfil.id());

            if (criterio == null) {
                existeCandidatoIndeterminado = true;
                continue;
            }

            EvaluacionCriterios evaluacion =
                    evaluarCriterios(perfil, criterio, datosFDS);

            if (evaluacion == EvaluacionCriterios.NO_COINCIDE) {
                continue;
            }

            if (perfil.estado() != EstadoPerfil.VALIDADO
                    || perfil.modoDivision() == ModoDivision.PENDIENTE
                    || criterio.estadoRegla() != EstadoReglaPerfil.VALIDADO) {
                existeCandidatoIndeterminado = true;
                continue;
            }

            if (evaluacion == EvaluacionCriterios.INDETERMINADO) {
                existeCandidatoIndeterminado = true;
                continue;
            }

            candidatos.add(perfil);
        }

        if (existeCandidatoIndeterminado) {
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

        if (especificos.size() == 1) {
            return ResultadoIdentificacionPerfil.identificado(especificos.get(0));
        }

        if (especificos.size() > 1) {
            return ResultadoIdentificacionPerfil.noDeterminado();
        }

        if (generales.size() == 1) {
            return ResultadoIdentificacionPerfil.identificado(generales.get(0));
        }

        return ResultadoIdentificacionPerfil.noDeterminado();
    }

    private EvaluacionCriterios evaluarCriterios(
            PerfilMatriz perfil,
            CriteriosIdentificacionPerfil criterio,
            DatosRelevantesFDS datosFDS) {

        EvaluacionCriterios numeroPerfil =
                evaluarNumeroPerfil(perfil.numeroOnu(), datosFDS.numeroOnu());

        if (numeroPerfil == EvaluacionCriterios.NO_COINCIDE) {
            return numeroPerfil;
        }

        EvaluacionCriterios numeros =
                evaluarNumerosOnu(criterio.numerosOnu(), datosFDS.numeroOnu());

        if (numeros == EvaluacionCriterios.NO_COINCIDE) {
            return numeros;
        }

        EvaluacionCriterios divisiones =
                evaluarDivisiones(
                        criterio.divisionesOnu(),
                        datosFDS.divisionPrincipalOnu()
                );

        if (divisiones == EvaluacionCriterios.NO_COINCIDE) {
            return divisiones;
        }

        EvaluacionCriterios pictogramas =
                evaluarPictogramas(
                        criterio.pictogramasRequeridos(),
                        datosFDS.pictogramasSGA()
                );

        if (pictogramas == EvaluacionCriterios.NO_COINCIDE) {
            return pictogramas;
        }

        if (numeroPerfil == EvaluacionCriterios.INDETERMINADO
                || numeros == EvaluacionCriterios.INDETERMINADO
                || divisiones == EvaluacionCriterios.INDETERMINADO
                || pictogramas == EvaluacionCriterios.INDETERMINADO) {
            return EvaluacionCriterios.INDETERMINADO;
        }

        return EvaluacionCriterios.COINCIDE;
    }

    private EvaluacionCriterios evaluarNumeroPerfil(
            String numeroPerfil,
            Optional<String> numeroFDS) {

        if (numeroPerfil == null) {
            return EvaluacionCriterios.COINCIDE;
        }

        if (numeroFDS.isEmpty()) {
            return EvaluacionCriterios.INDETERMINADO;
        }

        return numeroPerfil.equals(numeroFDS.get())
                ? EvaluacionCriterios.COINCIDE
                : EvaluacionCriterios.NO_COINCIDE;
    }

    private EvaluacionCriterios evaluarNumerosOnu(
            CriterioConjunto<String> criterio,
            Optional<String> numeroFDS) {

        if (criterio.aplicabilidad() == AplicabilidadCriterio.NO_APLICA) {
            return EvaluacionCriterios.COINCIDE;
        }

        if (numeroFDS.isEmpty()) {
            return EvaluacionCriterios.INDETERMINADO;
        }

        return criterio.valores().contains(numeroFDS.get())
                ? EvaluacionCriterios.COINCIDE
                : EvaluacionCriterios.NO_COINCIDE;
    }

    private EvaluacionCriterios evaluarDivisiones(
            CriterioConjunto<String> criterio,
            Optional<String> divisionPrincipalFDS) {

        if (criterio.aplicabilidad() == AplicabilidadCriterio.NO_APLICA) {
            return EvaluacionCriterios.COINCIDE;
        }

        if (divisionPrincipalFDS.isEmpty()) {
            return EvaluacionCriterios.INDETERMINADO;
        }

        return criterio.valores().contains(divisionPrincipalFDS.get())
                ? EvaluacionCriterios.COINCIDE
                : EvaluacionCriterios.NO_COINCIDE;
    }

    private EvaluacionCriterios evaluarPictogramas(
            CriterioConjunto<CodigoPictogramaSGA> criterio,
            Optional<Set<CodigoPictogramaSGA>> pictogramasFDS) {

        if (criterio.aplicabilidad() == AplicabilidadCriterio.NO_APLICA) {
            return EvaluacionCriterios.COINCIDE;
        }

        if (pictogramasFDS.isEmpty()) {
            return EvaluacionCriterios.INDETERMINADO;
        }

        return pictogramasFDS.get().containsAll(criterio.valores())
                ? EvaluacionCriterios.COINCIDE
                : EvaluacionCriterios.NO_COINCIDE;
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

    private enum EvaluacionCriterios {
        COINCIDE,
        NO_COINCIDE,
        INDETERMINADO
    }
}
