package Modelo.geometria;

public class Caja {
    private final Posicion centro;
    private final Dimension dimension;

    public Caja(Posicion centro, Dimension dimension) {
        this.centro = centro;
        this.dimension = dimension;
    }

    public boolean intersecta(Caja otra) {
        return izquierda() <= otra.derecha()
                && derecha() >= otra.izquierda()
                && arriba() <= otra.abajo()
                && abajo() >= otra.arriba();
    }

    public boolean contiene(Posicion posicion) {
        return posicion.obtenerX() >= izquierda()
                && posicion.obtenerX() <= derecha()
                && posicion.obtenerY() >= arriba()
                && posicion.obtenerY() <= abajo();
    }

    public double izquierda() {
        return centro.obtenerX() - dimension.obtenerAncho() / 2;
    }

    public double derecha() {
        return centro.obtenerX() + dimension.obtenerAncho() / 2;
    }

    public double arriba() {
        return centro.obtenerY() - dimension.obtenerAlto() / 2;
    }

    public double abajo() {
        return centro.obtenerY() + dimension.obtenerAlto() / 2;
    }
}
