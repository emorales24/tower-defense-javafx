package Modelo.nivel;

import Modelo.enemigo.TipoEnemigo;
import Modelo.geometria.Posicion;

public class EventoEnemigoEliminado {
    private final TipoEnemigo tipoEnemigo;
    private final Posicion posicion;
    private final boolean llegoALaBase;

    public EventoEnemigoEliminado(TipoEnemigo tipoEnemigo, Posicion posicion, boolean llegoALaBase) {
        if (tipoEnemigo == null || posicion == null) {
            throw new IllegalArgumentException("El evento requiere tipo de enemigo y posicion");
        }

        this.tipoEnemigo = tipoEnemigo;
        this.posicion = posicion;
        this.llegoALaBase = llegoALaBase;
    }

    public TipoEnemigo obtenerTipoEnemigo() {
        return tipoEnemigo;
    }

    public Posicion obtenerPosicion() {
        return posicion;
    }

    public boolean llegoALaBase() {
        return llegoALaBase;
    }
}
