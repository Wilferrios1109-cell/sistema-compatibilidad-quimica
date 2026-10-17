package com.sistemaquimico.domain.perfil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

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
            Optional<Set<String>> numerosOnu,
            Optional<Set<String>> divisionesOnu,
            Optional<Set<CodigoPictogramaSGA>> pictogramas,
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
            Optional<Set<String>> divisionesOnu,
            Optional<String> numeroOnu,
            Optional<Set<CodigoPictogramaSGA>> pictogramas) {

        return new DatosRelevantesFDS(
                claseOnu,
                divisionesOnu,
                numeroOnu,
                pictogramas
        );
    }

   
    private DatosRelevantesFDS datosClaseDos(
            Set<String> divisiones,
            Set<CodigoPictogramaSGA> pictogramas) {

            Optional<Set<String>> divisionesFDS = divisiones.isEmpty()
            ? Optional.empty()
            : Optional.of(divisiones);

            Optional<Set<CodigoPictogramaSGA>> pictogramasFDS =
            pictogramas.isEmpty()
                    ? Optional.empty()
                    : Optional.of(pictogramas);

    return crearDatosFDS(
            Optional.of(2),
            divisionesFDS,
            Optional.empty(),
            pictogramasFDS
    );
}


    @Test
    void debeIdentificarUnPerfilGeneralUnico() {
        PerfilMatriz perfil = crearPerfil(
                UUID.randomUUID(),
                "C-GENERAL",
                ModoDivision.GENERAL,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-GENERAL",
                ModoDivision.GENERAL,
                EstadoPerfil.VALIDADO
        );

        PerfilMatriz especifico = crearPerfil(
                UUID.randomUUID(),
                "C-ESPECIFICO",
                ModoDivision.LISTA_ESPECIFICA,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
                general.id(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
                especifico.id(),
                Optional.empty(),
                Optional.of(Set.of("2.2")),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-A",
                ModoDivision.LISTA_ESPECIFICA,
                EstadoPerfil.VALIDADO
        );

        PerfilMatriz perfilB = crearPerfil(
                UUID.randomUUID(),
                "C-B",
                ModoDivision.LISTA_ESPECIFICA,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosA = crearCriterios(
                perfilA.id(),
                Optional.empty(),
                Optional.of(Set.of("2.1", "2.2")),
                Optional.empty(),
                EstadoReglaPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criteriosB = crearCriterios(
                perfilB.id(),
                Optional.empty(),
                Optional.of(Set.of("2.2", "2.3")),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-PENDIENTE",
                ModoDivision.GENERAL,
                EstadoPerfil.PENDIENTE
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-GENERAL",
                ModoDivision.GENERAL,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-REGLA-PENDIENTE",
                ModoDivision.GENERAL,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
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
                UUID.randomUUID(),
                "C-DIVISION-22",
                ModoDivision.LISTA_ESPECIFICA,
                EstadoPerfil.VALIDADO
        );

        CriteriosIdentificacionPerfil criterios = crearCriterios(
                perfil.id(),
                Optional.empty(),
                Optional.of(Set.of("2.2")),
                Optional.empty(),
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
            UUID.randomUUID(),
            "C-GENERAL",
            ModoDivision.GENERAL,
            EstadoPerfil.VALIDADO
    );

    PerfilMatriz especifico = crearPerfil(
            UUID.randomUUID(),
            "C-ESPECIFICO",
            ModoDivision.LISTA_ESPECIFICA,
            EstadoPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
            general.id(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            EstadoReglaPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
            especifico.id(),
            Optional.empty(),
            Optional.of(Set.of("2.1")),
            Optional.empty(),
            EstadoReglaPerfil.PENDIENTE
    );

    ResultadoIdentificacionPerfil resultado = matcher.identificar(
            TipoPerfil.COLUMNA,
            datosClaseDos(Set.of("2.2"), Set.of()),
            List.of(general, especifico),
            List.of(criteriosGeneral, criteriosEspecificos)
    );

    assertEquals(EstadoIdentificacionPerfil.IDENTIFICADO, resultado.estado());
    assertEquals(general, resultado.perfil().orElseThrow());
}

    @Test
    void unaReglaEspecificaPendienteQueCoincideDebeImpedirLaIdentificacionAutomatica() {
    PerfilMatriz general = crearPerfil(
            UUID.randomUUID(),
            "C-GENERAL",
            ModoDivision.GENERAL,
            EstadoPerfil.VALIDADO
    );

    PerfilMatriz especifico = crearPerfil(
            UUID.randomUUID(),
            "C-ESPECIFICO",
            ModoDivision.LISTA_ESPECIFICA,
            EstadoPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criteriosGeneral = crearCriterios(
            general.id(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            EstadoReglaPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criteriosEspecificos = crearCriterios(
            especifico.id(),
            Optional.empty(),
            Optional.of(Set.of("2.2")),
            Optional.empty(),
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
            UUID.randomUUID(),
            "C-ESPECIFICO",
            ModoDivision.LISTA_ESPECIFICA,
            EstadoPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criterios = crearCriterios(
            especifico.id(),
            Optional.empty(),
            Optional.of(Set.of("2.2")),
            Optional.empty(),
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
            UUID.randomUUID(),
            "C-GENERAL",
            ModoDivision.GENERAL,
            EstadoPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criterios = crearCriterios(
            general.id(),
            Optional.of(Set.of("1000")),
            Optional.empty(),
            Optional.empty(),
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
            UUID.randomUUID(),
            "C-GENERAL",
            ModoDivision.GENERAL,
            EstadoPerfil.VALIDADO
    );

    CriteriosIdentificacionPerfil criterios = crearCriterios(
            general.id(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(Set.of(CodigoPictogramaSGA.GHS02)),
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

}
