package Modelo.geometria;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PosicionTest {

    @Test
    void moverHaciaAvanzaEnLaDireccionCorrectaSinPasarse() {
        Posicion origen = new Posicion(10, 0);
        Posicion destino = new Posicion(100, 0);

        Posicion nuevaPosicion = origen.moverHacia(destino, 10);

        assertEquals(20, nuevaPosicion.obtenerX(), 0.0001);
        assertEquals(0, nuevaPosicion.obtenerY(), 0.0001);
    }

    @Test
    void moverHaciaDevuelveElDestinoSiLaDistanciaAlcanza() {
        Posicion origen = new Posicion(90, 0);
        Posicion destino = new Posicion(100, 0);

        Posicion nuevaPosicion = origen.moverHacia(destino, 20);

        assertEquals(100, nuevaPosicion.obtenerX(), 0.0001);
        assertEquals(0, nuevaPosicion.obtenerY(), 0.0001);
    }

    @Test
    void moverHaciaNoAceptaDistanciasNegativas() {
        Posicion origen = new Posicion(0, 0);
        Posicion destino = new Posicion(10, 10);

        assertThrows(IllegalArgumentException.class, () -> origen.moverHacia(destino, -1));
    }

    @Test
    void distanciaASeCalculaCorrectamente() {
        Posicion una = new Posicion(0, 0);
        Posicion otra = new Posicion(3, 4);

        assertEquals(5, una.distanciaA(otra), 0.0001);
    }

    @Test
    void estaCercaDeRespetaLaTolerancia() {
        Posicion una = new Posicion(0, 0);
        Posicion otra = new Posicion(3, 4);

        assertTrue(una.estaCercaDe(otra, 5));
    }
}
