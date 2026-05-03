package Modelo;

import Modelo.escena.Base;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.geometria.Posicion;
import Modelo.nivel.Nivel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JuegoTest {

    @Test
    void alGanarUnNivelIntermedioNoAvanzaHastaQueSeLoIndique() {
        Juego juego = new Juego(new Jugador(), List.of(crearNivelSinEnemigos(), crearNivelSinEnemigos()));

        juego.iniciar();
        juego.actualizar(0.1);

        assertEquals(EstadoJuego.VICTORIA_NIVEL, juego.obtenerEstado());
        assertEquals(0, juego.obtenerIndiceNivelActual());

        juego.avanzarAlSiguienteNivel();

        assertEquals(EstadoJuego.EN_CURSO, juego.obtenerEstado());
        assertEquals(1, juego.obtenerIndiceNivelActual());
    }

    @Test
    void alGanarElUltimoNivelElJuegoQuedaEnVictoriaFinal() {
        Juego juego = new Juego(new Jugador(), List.of(crearNivelSinEnemigos()));

        juego.iniciar();
        juego.actualizar(0.1);

        assertEquals(EstadoJuego.VICTORIA_FINAL, juego.obtenerEstado());
        assertEquals(0, juego.obtenerIndiceNivelActual());
    }

    private Nivel crearNivelSinEnemigos() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Escenario escenario = new Escenario(
                120,
                50,
                ruta,
                new Posicion(0, 0),
                new Base(new Posicion(100, 0), 10),
                List.of()
        );

        return new Nivel(escenario, new Jugador(), 100, List.of(), Map.of());
    }
}
