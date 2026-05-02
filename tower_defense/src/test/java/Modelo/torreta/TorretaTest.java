package Modelo.torreta;

import Modelo.enemigo.Enemigo;
import Modelo.enemigo.EnemigoDebil;
import Modelo.enemigo.EnemigoFuerte;
import Modelo.escena.Ruta;
import Modelo.geometria.Posicion;
import Modelo.proyectil.Proyectil;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TorretaTest {

    @Test
    void eligeAlEnemigoMasCercanoDentroDelAlcance() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(200, 0)));
        Torreta torreta = new TorretaSimple(new Posicion(0, 0));
        Enemigo cercano = new EnemigoDebil(ruta);
        Enemigo lejano = new EnemigoFuerte(ruta);

        cercano.actualizar(0.2);
        lejano.actualizar(1.0);

        Optional<Enemigo> objetivo = torreta.elegirObjetivo(List.of(lejano, cercano));

        assertTrue(objetivo.isPresent());
        assertEquals(cercano, objetivo.get());
    }

    @Test
    void disparaCuandoElCooldownYElAlcanceLoPermiten() {
        Ruta ruta = new Ruta(List.of(new Posicion(0, 0), new Posicion(200, 0)));
        Torreta torreta = new TorretaSimple(new Posicion(0, 0));
        Enemigo enemigo = new EnemigoDebil(ruta);

        torreta.actualizar(1.0);
        Optional<Proyectil> disparo = torreta.dispararSiPuede(List.of(enemigo));

        assertTrue(disparo.isPresent());
        assertEquals(1, disparo.get().obtenerDanio(), 0.0001);
    }
}
