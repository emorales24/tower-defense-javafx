package Modelo.escena;

import Modelo.geometria.Posicion;

public class Slot {
    private final Posicion posicion;

    public Slot(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        this.posicion = posicion;
    }

    public Posicion obtenerPosicion() {
        return posicion;
    }
}
