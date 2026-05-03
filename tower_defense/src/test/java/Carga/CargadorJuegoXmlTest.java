package Carga;

import Modelo.EstadoJuego;
import Modelo.Juego;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CargadorJuegoXmlTest {

    @Test
    void cargaJuegoConLosTresNivelesPorDefecto() {
        CargadorJuegoXml cargador = new CargadorJuegoXml();

        Juego juego = cargador.cargar();

        assertEquals(EstadoJuego.MENU_PRINCIPAL, juego.obtenerEstado());
        assertEquals(3, juego.obtenerNiveles().size());
        assertEquals(0, juego.obtenerIndiceNivelActual());
    }

    @Test
    void todosLosNivelesCargadosCompartenElMismoJugador() {
        CargadorJuegoXml cargador = new CargadorJuegoXml();

        Juego juego = cargador.cargar();

        assertSame(juego.obtenerJugador(), juego.obtenerNiveles().get(0).obtenerJugador());
        assertSame(juego.obtenerJugador(), juego.obtenerNiveles().get(1).obtenerJugador());
        assertSame(juego.obtenerJugador(), juego.obtenerNiveles().get(2).obtenerJugador());
    }

    @Test
    void cargaJuegoConRutasIndicadas() {
        CargadorJuegoXml cargador = new CargadorJuegoXml();

        Juego juego = cargador.cargar(List.of("niveles/nivel1.xml", "niveles/nivel2.xml"));

        assertEquals(2, juego.obtenerNiveles().size());
    }

    @Test
    void fallaSiNoSeIndicanNiveles() {
        CargadorJuegoXml cargador = new CargadorJuegoXml();

        assertThrows(
                IllegalArgumentException.class,
                () -> cargador.cargar(List.of())
        );
    }
}
