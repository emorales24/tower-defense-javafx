package Modelo;

import Modelo.nivel.EstadoNivel;
import Modelo.nivel.Nivel;

import java.util.List;

public class Juego {
    private final Jugador jugador;
    private final List<Nivel> niveles;

    private int indiceNivelActual;
    private EstadoJuego estado;

    public Juego(Jugador jugador, List<Nivel> niveles) {
        if (jugador == null || niveles == null || niveles.isEmpty()) {
            throw new IllegalArgumentException("El juego requiere un jugador y al menos un nivel");
        }

        this.jugador = jugador;
        this.niveles = List.copyOf(niveles);
        this.indiceNivelActual = 0;
        this.estado = EstadoJuego.MENU_PRINCIPAL;
    }

    public void iniciar() {
        indiceNivelActual = 0;
        obtenerNivelActual().iniciar();
        estado = EstadoJuego.EN_CURSO;
    }

    public void actualizar(double deltaTiempo) {
        Actualizable.validarDeltaTiempo(deltaTiempo);

        if (estado != EstadoJuego.EN_CURSO) {
            return;
        }

        Nivel nivelActual = obtenerNivelActual();
        nivelActual.actualizar(deltaTiempo);

        if (nivelActual.obtenerEstado() == EstadoNivel.GANADO) {
            if (indiceNivelActual == niveles.size() - 1) {
                estado = EstadoJuego.VICTORIA_FINAL;
            } else {
                indiceNivelActual++;
                obtenerNivelActual().iniciar();
            }
        } else if (nivelActual.obtenerEstado() == EstadoNivel.PERDIDO) {
            estado = EstadoJuego.DERROTA;
        }
    }

    public Jugador obtenerJugador() {
        return jugador;
    }

    public Nivel obtenerNivelActual() {
        return niveles.get(indiceNivelActual);
    }

    public EstadoJuego obtenerEstado() {
        return estado;
    }

    public int obtenerIndiceNivelActual() {
        return indiceNivelActual;
    }

    public List<Nivel> obtenerNiveles() {
        return niveles;
    }
}
