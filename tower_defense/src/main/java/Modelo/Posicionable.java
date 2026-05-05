package Modelo;

import Modelo.geometria.Caja;
import Modelo.geometria.Posicion;

public interface Posicionable {
    Posicion obtenerPosicion();

    Caja obtenerCajaDeColision();

    default double distanciaA(Posicionable otro) {
        return obtenerPosicion().distanciaA(otro.obtenerPosicion());
    }

    default boolean colisionaCon(Posicionable otro) {
        return obtenerCajaDeColision().intersecta(otro.obtenerCajaDeColision());
    }

    default boolean contiene(Posicion posicion) {
        return obtenerCajaDeColision().contiene(posicion);
    }
}
