package Modelo.escena;

import Modelo.Daniable;
import Modelo.geometria.Posicion;

public class Base implements Daniable {
    private final Posicion posicion;
    private final double vidaMaxima;
    private double vidaActual;

    public Base(Posicion posicion, double vidaMaxima) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        if (Double.isNaN(vidaMaxima) || Double.isInfinite(vidaMaxima) || vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida maxima debe ser positiva y finita");
        }

        this.posicion = posicion;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
    }

    public Posicion obtenerPosicion() {
        return posicion;
    }

    @Override
    public void recibirDanio(double cantidadDanio) {
        Daniable.validarDanio(cantidadDanio);
        vidaActual = Math.max(0, vidaActual - cantidadDanio);
    }

    @Override
    public double obtenerVidaActual() {
        return vidaActual;
    }

    @Override
    public double obtenerVidaMaxima() {
        return vidaMaxima;
    }

    public void reiniciarVida() {
        vidaActual = vidaMaxima;
    }
}
