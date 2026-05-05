package Modelo.escena;

import Modelo.Posicionable;
import Modelo.geometria.Caja;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;

public class Slot implements Posicionable {
    private static final Dimension DIMENSION_POR_DEFECTO = new Dimension(32, 32);

    private final Posicion posicion;
    private final Dimension dimension;

    public Slot(Posicion posicion) {
        this(posicion, DIMENSION_POR_DEFECTO);
    }

    public Slot(Posicion posicion, Dimension dimension) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        if (dimension == null) {
            throw new IllegalArgumentException("La dimension no puede ser null");
        }

        this.posicion = posicion;
        this.dimension = dimension;
    }

    @Override
    public Posicion obtenerPosicion() {
        return posicion;
    }

    public Dimension obtenerDimension() {
        return dimension;
    }

    @Override
    public Caja obtenerCajaDeColision() {
        return new Caja(posicion, dimension);
    }
}
