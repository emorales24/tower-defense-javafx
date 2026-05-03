package Modelo.nivel;

import Modelo.Jugador;
import Modelo.enemigo.TipoEnemigo;
import Modelo.escena.Base;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.torreta.TipoTorreta;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NivelTest {

    @Test
    void nivelOtorgaRecompensaYSeGanaCuandoNoQuedanEnemigosNiSpawns() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(400, 0)));
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(400, 0), 10);
        Slot slot = new Slot(new Posicion(50, 40));
        Escenario escenario = new Escenario(420, 50, ruta, puntoSpawn, baseJugador, List.of(slot));
        Jugador jugador = new Jugador();
        Nivel nivel = new Nivel(
                escenario,
                jugador,
                100,
                List.of(new SpawnEnemigo(TipoEnemigo.DEBIL, 0)),
                Map.of(slot, TipoTorreta.POTENTE)
        );

        nivel.iniciar();

        for (int i = 0; i < 30 && nivel.obtenerEstado() == EstadoNivel.EN_CURSO; i++) {
            nivel.actualizar(0.1);
        }

        assertEquals(EstadoNivel.GANADO, nivel.obtenerEstado());
        assertEquals(108, jugador.obtenerDineroDisponible(), 0.0001);
        assertEquals(8, jugador.obtenerPuntajeAcumulado(), 0.0001);
    }

    @Test
    void nivelSePierdeSiUnEnemigoLlegaALaBase() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(10, 0)));
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(10, 0), 1);
        Escenario escenario = new Escenario(20, 20, ruta, puntoSpawn, baseJugador, List.of());
        Jugador jugador = new Jugador();
        Nivel nivel = new Nivel(
                escenario,
                jugador,
                100,
                List.of(new SpawnEnemigo(TipoEnemigo.DEBIL, 0)),
                Map.of()
        );

        nivel.iniciar();
        nivel.actualizar(1.0);

        assertEquals(EstadoNivel.PERDIDO, nivel.obtenerEstado());
        assertTrue(baseJugador.estaDestruido());
    }

    @Test
    void noInstalaTorretaSiNoHayDineroSuficiente() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(100, 0), 10);
        Slot slot = new Slot(new Posicion(50, 40));
        Escenario escenario = new Escenario(120, 50, ruta, puntoSpawn, baseJugador, List.of(slot));
        Jugador jugador = new Jugador();
        Nivel nivel = new Nivel(escenario, jugador, 10, List.of(), Map.of());

        nivel.iniciar();
        boolean seInstalo = nivel.instalarTorreta(slot, TipoTorreta.SIMPLE);

        assertFalse(seInstalo);
        assertTrue(nivel.obtenerTorretasInstaladas().isEmpty());
    }
}
