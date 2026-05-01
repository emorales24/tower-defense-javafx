package Modelo.proyectil;

import Modelo.Actualizable;
import Modelo.Posicionable;
import Modelo.enemigo.Enemigo;
import Modelo.geometria.Caja;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;
import Modelo.torreta.TipoTorreta;

public class Proyectil implements Actualizable, Posicionable {
    private final TipoTorreta tipoTorreta;
    private final double velocidad;
    private final double danio;
    private final Dimension dimension;

    private Posicion posicion;
    private Enemigo objetivo;
    private boolean activo;

    public Proyectil(Posicion posicion, Enemigo objetivo, double velocidad, double danio, Dimension dimension) {
        this(posicion, objetivo, velocidad, danio, dimension, TipoTorreta.SIMPLE);
    }

    public Proyectil(Posicion posicion, Enemigo objetivo, double velocidad, double danio, Dimension dimension, TipoTorreta tipoTorreta) {
        if (posicion == null || objetivo == null || dimension == null) {
            throw new IllegalArgumentException("El proyectil requiere posicion, objetivo y dimension");
        }

        if (tipoTorreta == null) {
            throw new IllegalArgumentException("El tipo de torreta no puede ser null");
        }

        if (Double.isNaN(velocidad) || Double.isInfinite(velocidad) || velocidad < 0) {
            throw new IllegalArgumentException("La velocidad debe ser no negativa y finita");
        }

        if (Double.isNaN(danio) || Double.isInfinite(danio) || danio < 0) {
            throw new IllegalArgumentException("El danio debe ser no negativo y finito");
        }

        this.posicion = posicion;
        this.objetivo = objetivo;
        this.tipoTorreta = tipoTorreta;
        this.velocidad = velocidad;
        this.danio = danio;
        this.dimension = dimension;
        this.activo = true;
    }

    @Override
    public Posicion obtenerPosicion() {
        return posicion;
    }

    @Override
    public Caja obtenerCajaDeColision() {
        return new Caja(posicion, dimension);
    }

    @Override
    public void actualizar(double deltaTiempo) {
        Actualizable.validarDeltaTiempo(deltaTiempo);

        if (!activo) {
            return;
        }

        if (objetivo == null || !objetivo.estaActivo()) {
            activo = false;
            return;
        }

        double distanciaARecorrer = velocidad * deltaTiempo;
        posicion = posicion.moverHacia(objetivo.obtenerPosicion(), distanciaARecorrer);

        if (colisionaCon(objetivo)) {
            objetivo.recibirDanio(danio);
            activo = false;
        }
    }

    public double obtenerVelocidad() {
        return velocidad;
    }

    public double obtenerDanio() {
        return danio;
    }

    public TipoTorreta obtenerTipoTorreta() {
        return tipoTorreta;
    }

    public Enemigo obtenerObjetivo() {
        return objetivo;
    }

    public boolean estaActivo() {
        return activo;
    }
}
