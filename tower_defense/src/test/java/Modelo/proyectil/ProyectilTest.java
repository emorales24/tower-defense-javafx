package Modelo.proyectil;

import Modelo.enemigo.Enemigo;
import Modelo.enemigo.EnemigoDebil;
import Modelo.escena.Ruta;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProyectilTest {

    @Test
    void proyectilImpactaAlObjetivoYSeDesactiva() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Enemigo enemigo = new EnemigoDebil(ruta);
        Proyectil proyectil = new Proyectil(new Posicion(0, 0), enemigo, 300, 3, new Dimension(8, 8));

        proyectil.actualizar(0.5);

        assertFalse(proyectil.estaActivo());
        assertTrue(enemigo.estaDestruido());
    }

    @Test
    void proyectilSeDesactivaSiElObjetivoYaNoEstaActivo() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(100, 0)));
        Enemigo enemigo = new EnemigoDebil(ruta);
        enemigo.recibirDanio(3);
        Proyectil proyectil = new Proyectil(new Posicion(0, 0), enemigo, 300, 3, new Dimension(8, 8));

        proyectil.actualizar(0.1);

        assertFalse(proyectil.estaActivo());
    }
}
