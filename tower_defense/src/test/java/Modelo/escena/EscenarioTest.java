package Modelo.escena;

import Modelo.geometria.Posicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EscenarioTest {

    @Test
    void contieneDevuelveTrueParaPosicionDentroDelEscenario() {
        Escenario escenario = crearEscenarioValido();

        assertTrue(escenario.contiene(new Posicion(50, 50)));
    }

    @Test
    void contieneDevuelveFalseParaPosicionFueraDelEscenario() {
        Escenario escenario = crearEscenarioValido();

        assertFalse(escenario.contiene(new Posicion(150, 50)));
    }

    @Test
    void noPermiteRutaQueNoEmpieceEnLaBaseEnemiga() {
        Ruta ruta = new Ruta(List.of(new Posicion(10, 0), new Posicion(100, 0)));
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(100, 0), 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> new Escenario(200, 100, ruta, puntoSpawn, baseJugador, List.of())
        );
    }

    @Test
    void noPermiteSlotsQueColisionenConLaRuta() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)), 20);
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(100, 0), 10);
        Slot slotSobreRuta = new Slot(new Posicion(50, 0));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Escenario(120, 100, ruta, puntoSpawn, baseJugador, List.of(slotSobreRuta))
        );
    }

    private Escenario crearEscenarioValido() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Posicion puntoSpawn = new Posicion(0, 0);
        Base baseJugador = new Base(new Posicion(100, 0), 10);
        Slot slot = new Slot(new Posicion(50, 50));
        return new Escenario(120, 100, ruta, puntoSpawn, baseJugador, List.of(slot));
    }
}
