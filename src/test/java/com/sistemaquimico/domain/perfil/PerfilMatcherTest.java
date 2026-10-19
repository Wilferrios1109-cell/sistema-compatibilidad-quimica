
package com.sistemaquimico.domain.perfil;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerfilMatcherTest {

    private final PerfilMatcher matcher = new PerfilMatcher();

    private PerfilMatriz crearPerfil(
            UUID id,
            String codigo,
            ModoDivision modo,
            EstadoPerfil estado) {

        return new PerfilMatriz(
                id,
                UUID.randomUUID(),
                codigo,
                1,
                "Perfil " + codigo,
                TipoPerfil.COLUMNA,
                2,
                null,
                null,
                estado,
                modo
        );
    }

    private CriteriosIdentificacionPerfil crearCriterios(
            UUID idPerfil,
            CriterioConjunto<String> numerosOnu,
            CriterioConjunto<String> divisionesOnu,
            CriterioConjunto<CodigoPictogramaSGA> pictogramas,
            EstadoReglaPerfil estado) {

        return new CriteriosIdentificacionPerfil(
                idPerfil,
                numerosOnu,
                divisionesOnu,
                pictogramas,
                estado
        );
    }

    private DatosRelevantesFDS crearDatosFDS(
            Optional<Integer> claseOnu,
            Optional<String> divisionPrincipalOnu,
            Optional<String> numeroOnu,
            Optional<Set<CodigoPictogramaSGA>> pictogramas) {

        return new DatosRelevantesFDS(
                claseOnu,
                divisionPrincipalOnu,
                numeroOnu,
                pictogramas
        );
    }

    private DatosRelevantesFDS datosClaseDos(
            Set<String> divisiones,
            Set<CodigoPictogramaSGA> pictogramas) {

        if (divisiones.size() > 1) {
            throw new IllegalArgumentException(
                    "La FDS debe tener una sola división principal"
            );
        }

        Optional<String> divisionPrincipal = divisiones.stream().findFirst();

        Optional<Set<CodigoPictogramaSGA>> pictogramasFDS =
                pictogramas.isEmpty()
                        ? Optional.empty()
                        : Optional.of(pictogramas);

        return crearDatosFDS(
                Optional.of(2),
                divisionPrincipal,
                Optional.empty(),
                pictogramasFDS
        );
    }

    private CriterioConjunto<String> sinRestriccionTexto() {
        return CriterioConjunto.noAplica();
    }

    private CriterioConjunto<String> divisionesPermitidas(
            String... divisiones) {
        return CriterioConjunto.aplica(Set.of(divisiones));
    }

    private CriterioConjunto<String> numerosPermitidos(String... numeros) {
        return CriterioConjunto.aplica(Set.of(numeros));
    }

    private CriterioConjunto<CodigoPictogramaSGA> sinRestriccionPictogramas() {
        return CriterioConjunto.noAplica();
    }

    private CriterioConjunto<CodigoPictogramaSGA> pictogramasRequeridos(
            CodigoPictogramaSGA... pictogramas) {
        return CriterioConjunto.aplica(Set.of(pictogramas));
    }

    @Test
    void debeIdentificarUnPerfilGeneralUnico() {
        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of(), Set.of()),
                List.of(perfil),
                List.of(criterios)
        );

        assertEquals(EstadoIdentificacionPerfil.IDENTIFICADO, resultado.estado());
        assertEquals(perfil, resultado.perfil().orElseThrow());
    }

    @Test
    void debePreferirElPerfilEspecificoSobreElGeneral() {
        PerfilMatriz general = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(), "C-ESPECIFICO",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
                general.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
                especifico.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of("2.2"), Set.of()),
                List.of(general, especifico),
                List.of(criteriosGeneral, criteriosEspecificos)
        );

        assertEquals(EstadoIdentificacionPerfil.IDENTIFICADO, resultado.estado());
        assertEquals(especifico, resultado.perfil().orElseThrow());
    }

    @Test
    void noDebeElegirEntrePerfilesEspecificosIncomparables() {
        PerfilMatriz perfilA = crearPerfil(
                UUID.randomUUID(), "C-A",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        PerfilMatriz perfilB = crearPerfil(
                UUID.randomUUID(), "C-B",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosA = crearCriterios(
                perfilA.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.1", "2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosB = crearCriterios(
                perfilB.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.2", "2.3"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of("2.2"), Set.of()),
                List.of(perfilA, perfilB),
                List.of(criteriosA, criteriosB)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
        assertTrue(resultado.perfil().isEmpty());
    }

    @Test
    void noDebeIdentificarUnPerfilPendienteDeValidacion() {
        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(), "C-PENDIENTE",
                ModoDivision.GENERAL, EstadoPerfil.PENDIENTE
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of(), Set.of()),
                List.of(perfil),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarCuandoNoHayCandidatos() {
        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of(), Set.of()),
                List.of(),
                List.of()
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarCuandoFaltaLaClaseOnu() {
        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(perfil),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarSiLaReglaEstaPendiente() {
        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(), "C-REGLA-PENDIENTE",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.PENDIENTE
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of(), Set.of()),
                List.of(perfil),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarSiFaltaUnDatoRequerido() {
        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(), "C-DIVISION-22",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(perfil),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void unaReglaEspecificaPendienteQueNoCoincideNoDebeBloquearElPerfilGeneral() {
        PerfilMatriz general = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(), "C-ESPECIFICO",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
                general.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
                especifico.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.1"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.PENDIENTE
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of("2.2"), Set.of()),
                List.of(general, especifico),
                List.of(criteriosGeneral, criteriosEspecificos)
        );

        assertEquals(
                EstadoIdentificacionPerfil.IDENTIFICADO,
                resultado.estado()
        );
        assertEquals(general, resultado.perfil().orElseThrow());
    }

    @Test
    void unaReglaEspecificaPendienteQueCoincideDebeImpedirLaIdentificacionAutomatica() {
        PerfilMatriz general = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(), "C-ESPECIFICO",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
                general.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
                especifico.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.PENDIENTE
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datosClaseDos(Set.of("2.2"), Set.of()),
                List.of(general, especifico),
                List.of(criteriosGeneral, criteriosEspecificos)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarPerfilEspecificoSiSeDesconoceLaDivision() {
        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(), "C-ESPECIFICO",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                especifico.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(especifico),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarPerfilSiElNumeroOnuNoCoincide() {
        PerfilMatriz general = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                general.id(),
                numerosPermitidos("1000"),
                sinRestriccionTexto(),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.of(2),
                Optional.empty(),
                Optional.of("2000"),
                Optional.empty()
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(general),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void noDebeIdentificarPerfilSiFaltaUnPictogramaRequerido() {
        PerfilMatriz general = crearPerfil(
                UUID.randomUUID(), "C-GENERAL",
                ModoDivision.GENERAL, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                general.id(),
                sinRestriccionTexto(),
                sinRestriccionTexto(),
                pictogramasRequeridos(CodigoPictogramaSGA.GHS02),
                EstadoReglaPerfil.VALIDADO
        );

        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.of(2),
                Optional.empty(),
                Optional.empty(),
                Optional.of(Set.of(CodigoPictogramaSGA.GHS04))
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(general),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.NO_DETERMINADO,
                resultado.estado()
        );
    }

    @Test
    void debeCoincidirConCualquieraDeLasDivisionesPermitidas() {
        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(), "C-GASES",
                ModoDivision.LISTA_ESPECIFICA, EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                especifico.id(),
                sinRestriccionTexto(),
                divisionesPermitidas("2.1", "2.2"),
                sinRestriccionPictogramas(),
                EstadoReglaPerfil.VALIDADO
        );

        DatosRelevantesFDS datos = crearDatosFDS(
                Optional.of(2),
                Optional.of("2.1"),
                Optional.empty(),
                Optional.empty()
        );

        ResultadoIdentificacionPerfil resultado = matcher.identificar(
                TipoPerfil.COLUMNA,
                datos,
                List.of(especifico),
                List.of(criterios)
        );

        assertEquals(
                EstadoIdentificacionPerfil.IDENTIFICADO,
                resultado.estado()
        );
        assertEquals(especifico, resultado.perfil().orElseThrow());
    }
}
