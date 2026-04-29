package Modelo.enemigo;

import Modelo.Actualizable;
import Modelo.Daniable;
import Modelo.Posicionable;
import Modelo.escena.Ruta;
import Modelo.geometria.Caja;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;

public abstract class Enemigo implements Actualizable, Daniable, Posicionable {
    private final TipoEnemigo tipo;
    private final Ruta ruta;
    private final double vidaMaxima;
    private final double velocidad;
    private final double recompensaDinero;
    private final double recompensaPuntaje;
    private final double danioABase;
    private final Dimension dimension;

    private Posicion posicion;
    private double vidaActual;
    private int indiceProximoPunto;
    private boolean activo;
    private boolean llegoAlFinal;

    protected Enemigo(
            TipoEnemigo tipo,
            Ruta ruta,
            double vidaMaxima,
            double velocidad,
            double recompensaDinero,
            double recompensaPuntaje,
            double danioABase,
            Dimension dimension
    ) {
        if (tipo == null || ruta == null || dimension == null) {
            throw new IllegalArgumentException("El enemigo requiere tipo, ruta y dimension");
        }

        if (Double.isNaN(vidaMaxima) || Double.isInfinite(vidaMaxima) || vidaMaxima <= 0) {
            throw new IllegalArgumentException("La vida maxima debe ser positiva y finita");
        }

        if (Double.isNaN(velocidad) || Double.isInfinite(velocidad) || velocidad < 0) {
            throw new IllegalArgumentException("La velocidad debe ser no negativa y finita");
        }

        if (Double.isNaN(recompensaDinero) || Double.isInfinite(recompensaDinero) || recompensaDinero < 0) {
            throw new IllegalArgumentException("La recompensa de dinero debe ser no negativa y finita");
        }

        if (Double.isNaN(recompensaPuntaje) || Double.isInfinite(recompensaPuntaje) || recompensaPuntaje < 0) {
            throw new IllegalArgumentException("La recompensa de puntaje debe ser no negativa y finita");
        }

        if (Double.isNaN(danioABase) || Double.isInfinite(danioABase) || danioABase < 0) {
            throw new IllegalArgumentException("El danio a base debe ser no negativo y finito");
        }

        this.tipo = tipo;
        this.ruta = ruta;
        this.vidaMaxima = vidaMaxima;
        this.velocidad = velocidad;
        this.recompensaDinero = recompensaDinero;
        this.recompensaPuntaje = recompensaPuntaje;
        this.danioABase = danioABase;
        this.dimension = dimension;
        this.posicion = ruta.obtenerInicio();
        this.vidaActual = vidaMaxima;
        this.indiceProximoPunto = 1;
        this.activo = true;
        this.llegoAlFinal = false;
    }

    public TipoEnemigo obtenerTipo() {
        return tipo;
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

        if (!activo || velocidad == 0 || indiceProximoPunto >= ruta.cantidadPuntos()) {
            return;
        }

        double distanciaARecorrer = velocidad * deltaTiempo;

        while (distanciaARecorrer > 0 && activo && indiceProximoPunto < ruta.cantidadPuntos()) {
            Posicion proximoPunto = ruta.obtenerPunto(indiceProximoPunto);
            double distanciaAlPunto = posicion.distanciaA(proximoPunto);

            if (distanciaARecorrer >= distanciaAlPunto) {
                posicion = proximoPunto;
                distanciaARecorrer -= distanciaAlPunto;
                indiceProximoPunto++;

                if (indiceProximoPunto >= ruta.cantidadPuntos()) {
                    activo = false;
                    llegoAlFinal = true;
                }
            } else {
                posicion = posicion.moverHacia(proximoPunto, distanciaARecorrer);
                distanciaARecorrer = 0;
            }
        }
    }

    @Override
    public void recibirDanio(double cantidadDanio) {
        Daniable.validarDanio(cantidadDanio);

        if (!activo) {
            return;
        }

        vidaActual = Math.max(0, vidaActual - cantidadDanio);

        if (estaDestruido()) {
            activo = false;
        }
    }

    @Override
    public double obtenerVidaActual() {
        return vidaActual;
    }

    @Override
    public double obtenerVidaMaxima() {
        return vidaMaxima;
    }

    public double obtenerVelocidad() {
        return velocidad;
    }

    public double obtenerRecompensaDinero() {
        return recompensaDinero;
    }

    public double obtenerRecompensaPuntaje() {
        return recompensaPuntaje;
    }

    public double obtenerDanioABase() {
        return danioABase;
    }

    public boolean estaActivo() {
        return activo;
    }

    public boolean llegoAlFinal() {
        return llegoAlFinal;
    }
}