package Modelo.enemigo;

import Modelo.escena.Ruta;
import Modelo.geometria.Posicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemigoTest {

    @Test
    void enemigoAvanzaSobreLaRuta() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Enemigo enemigo = new EnemigoDebil(ruta);

        enemigo.actualizar(0.5);

        assertEquals(50, enemigo.obtenerPosicion().obtenerX(), 0.0001);
        assertEquals(0, enemigo.obtenerPosicion().obtenerY(), 0.0001);
    }

    @Test
    void enemigoSeDesactivaCuandoLlegaAlFinal() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Enemigo enemigo = new EnemigoDebil(ruta);

        enemigo.actualizar(2.0);

        assertFalse(enemigo.estaActivo());
        assertTrue(enemigo.llegoAlFinal());
    }

    @Test
    void enemigoSeDesactivaCuandoSuVidaLlegaACero() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Enemigo enemigo = new EnemigoDebil(ruta);

        enemigo.recibirDanio(1);

        assertTrue(enemigo.estaDestruido());
        assertFalse(enemigo.estaActivo());
    }
}
