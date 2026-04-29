package Modelo.escena;

import Modelo.geometria.Posicion;

import java.util.List;

public class Ruta {
    private final List<Posicion> puntos;

    public Ruta(List<Posicion> puntos) {
        if (puntos == null || puntos.size() < 2) {
            throw new IllegalArgumentException("La ruta debe tener al menos dos puntos");
        }

        this.puntos = List.copyOf(puntos);
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
}
