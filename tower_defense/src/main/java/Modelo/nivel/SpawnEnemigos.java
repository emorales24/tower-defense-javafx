package Modelo.nivel;

import Modelo.enemigo.TipoEnemigo;

public class SpawnEnemigos {
    private final TipoEnemigo tipoEnemigo;
    private final double tiempoAparicion;

    public SpawnEnemigos(TipoEnemigo tipoEnemigo, double tiempoAparicion) {
        if (tipoEnemigo == null) {
            throw new IllegalArgumentException("El tipo de enemigo no puede ser null");
        }

        if (Double.isNaN(tiempoAparicion) || Double.isInfinite(tiempoAparicion) || tiempoAparicion < 0) {
            throw new IllegalArgumentException("El tiempo de aparicion debe ser no negativo y finito");
        }

        this.tipoEnemigo = tipoEnemigo;
        this.tiempoAparicion = tiempoAparicion;
    }

    public TipoEnemigo obtenerTipoEnemigo() {
        return tipoEnemigo;
    }

    public double obtenerTiempoAparicion() {
        return tiempoAparicion;
    }

    public boolean debeAparecer(double tiempoTranscurrido) {
        return tiempoTranscurrido >= tiempoAparicion;
    }
}
