package Modelo.nivel;

import Modelo.enemigo.Enemigo;
import Modelo.enemigo.EnemigoDebil;
import Modelo.enemigo.EnemigoFuerte;
import Modelo.enemigo.EnemigoMedio;
import Modelo.enemigo.TipoEnemigo;
import Modelo.escena.Ruta;

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

    public Enemigo crearEnemigo(Ruta ruta) {
        switch (tipoEnemigo) {
            case DEBIL:
                return new EnemigoDebil(ruta);
            case MEDIO:
                return new EnemigoMedio(ruta);
            case FUERTE:
                return new EnemigoFuerte(ruta);
            default:
                throw new IllegalStateException("Tipo de enemigo no soportado");
        }
    }
}
