package Modelo.torreta;

import Modelo.Actualizable;
import Modelo.Posicionable;
import Modelo.enemigo.Enemigo;
import Modelo.geometria.Caja;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;
import Modelo.proyectil.Proyectil;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class Torreta implements Actualizable, Posicionable {
    private final TipoTorreta tipo;
    private final Posicion posicion;
    private final double costo;
    private final double danioPorDisparo;
    private final double alcance;
    private final double intervaloEntreDisparos;
    private final double velocidadProyectil;
    private final Dimension dimension;
    private final Dimension dimensionProyectil;

    private double tiempoDesdeUltimoDisparo;

    protected Torreta(
            TipoTorreta tipo,
            Posicion posicion,
            double costo,
            double danioPorDisparo,
            double alcance,
            double intervaloEntreDisparos,
            double velocidadProyectil,
            Dimension dimension,
            Dimension dimensionProyectil
    ) {
        if (tipo == null || posicion == null || dimension == null || dimensionProyectil == null) {
            throw new IllegalArgumentException("La torreta requiere tipo, posicion y dimensiones");
        }

        if (Double.isNaN(costo) || Double.isInfinite(costo) || costo < 0) {
            throw new IllegalArgumentException("El costo debe ser no negativo y finito");
        }

        if (Double.isNaN(danioPorDisparo) || Double.isInfinite(danioPorDisparo) || danioPorDisparo < 0) {
            throw new IllegalArgumentException("El danio debe ser no negativo y finito");
        }

        if (Double.isNaN(alcance) || Double.isInfinite(alcance) || alcance < 0) {
            throw new IllegalArgumentException("El alcance debe ser no negativo y finito");
        }

        if (Double.isNaN(intervaloEntreDisparos) || Double.isInfinite(intervaloEntreDisparos) || intervaloEntreDisparos <= 0) {
            throw new IllegalArgumentException("El intervalo entre disparos debe ser positivo y finito");
        }

        if (Double.isNaN(velocidadProyectil) || Double.isInfinite(velocidadProyectil) || velocidadProyectil < 0) {
            throw new IllegalArgumentException("La velocidad del proyectil debe ser no negativa y finita");
        }

        this.tipo = tipo;
        this.posicion = posicion;
        this.costo = costo;
        this.danioPorDisparo = danioPorDisparo;
        this.alcance = alcance;
        this.intervaloEntreDisparos = intervaloEntreDisparos;
        this.velocidadProyectil = velocidadProyectil;
        this.dimension = dimension;
        this.dimensionProyectil = dimensionProyectil;
        this.tiempoDesdeUltimoDisparo = intervaloEntreDisparos;
    }

    public TipoTorreta obtenerTipo() {
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
        tiempoDesdeUltimoDisparo += deltaTiempo;
    }

    public double obtenerCosto() {
        return costo;
    }

    public double obtenerDanioPorDisparo() {
        return danioPorDisparo;
    }

    public double obtenerAlcance() {
        return alcance;
    }

    public double obtenerIntervaloEntreDisparos() {
        return intervaloEntreDisparos;
    }

    public boolean puedeDisparar() {
        return tiempoDesdeUltimoDisparo >= intervaloEntreDisparos;
    }

    public Optional<Enemigo> elegirObjetivo(List<Enemigo> enemigos) {
        if (enemigos == null) {
            throw new IllegalArgumentException("La lista de enemigos no puede ser null");
        }

        return enemigos.stream()
                .filter(Enemigo::estaActivo)
                .filter(enemigo -> posicion.distanciaA(enemigo.obtenerPosicion()) <= alcance)
                .min(Comparator.comparingDouble(this::distanciaA));
    }

    public Optional<Proyectil> dispararSiPuede(List<Enemigo> enemigos) {
        if (!puedeDisparar()) {
            return Optional.empty();
        }

        Optional<Enemigo> objetivo = elegirObjetivo(enemigos);
        if (objetivo.isEmpty()) {
            return Optional.empty();
        }

        tiempoDesdeUltimoDisparo = 0;
        return Optional.of(new Proyectil(
                posicion,
                objetivo.get(),
                velocidadProyectil,
                danioPorDisparo,
                dimensionProyectil
        ));
    }
}