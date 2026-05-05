package Modelo.escena;

import Modelo.geometria.Posicion;

import java.util.List;

public class Ruta {
    private static final double ANCHO_POR_DEFECTO = 32;

    private final List<Posicion> puntos;
    private final double ancho;

    public Ruta(List<Posicion> puntos) {
        this(puntos, ANCHO_POR_DEFECTO);
    }

    public Ruta(List<Posicion> puntos, double ancho) {
        if (puntos == null || puntos.size() < 2) {
            throw new IllegalArgumentException("La ruta debe tener al menos dos puntos");
        }

        if (Double.isNaN(ancho) || Double.isInfinite(ancho) || ancho <= 0) {
            throw new IllegalArgumentException("El ancho de la ruta debe ser positivo y finito");
        }

        this.puntos = List.copyOf(puntos);
        this.ancho = ancho;
    }

    public Posicion obtenerInicio() {
        return puntos.get(0);
    }

    public Posicion obtenerFin() {
        return puntos.get(puntos.size() - 1);
    }

    public Posicion obtenerPunto(int indice) {
        return puntos.get(indice);
    }

    public int cantidadPuntos() {
        return puntos.size();
    }

    public List<Posicion> obtenerPuntos() {
        return puntos;
    }

    public double obtenerAncho() {
        return ancho;
    }
}
