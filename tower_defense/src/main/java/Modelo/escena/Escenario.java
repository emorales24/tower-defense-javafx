package Modelo.escena;

import Modelo.geometria.Posicion;

import java.util.List;

public class Escenario {
    private final double ancho;
    private final double alto;
    private final Ruta ruta;
    private final Base baseEnemiga;
    private final Base baseJugador;
    private final List<Slot> slots;

    public Escenario(double ancho, double alto, Ruta ruta, Base baseEnemiga, Base baseJugador, List<Slot> slots) {
        if (Double.isNaN(ancho) || Double.isInfinite(ancho) || ancho <= 0) {
            throw new IllegalArgumentException("El ancho debe ser positivo y finito");
        }

        if (Double.isNaN(alto) || Double.isInfinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("El alto debe ser positivo y finito");
        }

        if (ruta == null || baseEnemiga == null || baseJugador == null || slots == null) {
            throw new IllegalArgumentException("Los componentes del escenario no pueden ser null");
        }

        this.ancho = ancho;
        this.alto = alto;
        this.ruta = ruta;
        this.baseEnemiga = baseEnemiga;
        this.baseJugador = baseJugador;
        this.slots = List.copyOf(slots);

        validarElementosDentroDelEscenario();
        validarCoherenciaEntreBasesYRuta();
    }

    public double obtenerAncho() {
        return ancho;
    }

    public double obtenerAlto() {
        return alto;
    }

    public Ruta obtenerRuta() {
        return ruta;
    }

    public Base obtenerBaseEnemiga() {
        return baseEnemiga;
    }

    public Base obtenerBaseJugador() {
        return baseJugador;
    }

    public List<Slot> obtenerSlots() {
        return slots;
    }

    public boolean contiene(Slot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("El slot no puede ser null");
        }

        return slots.contains(slot);
    }

    public boolean contiene(Posicion posicion) {
        if (posicion == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        return posicion.obtenerX() >= 0
                && posicion.obtenerX() <= ancho
                && posicion.obtenerY() >= 0
                && posicion.obtenerY() <= alto;
    }

    private void validarElementosDentroDelEscenario() {
        if (!contiene(baseEnemiga.obtenerPosicion()) || !contiene(baseJugador.obtenerPosicion())) {
            throw new IllegalArgumentException("Las bases deben estar dentro del escenario");
        }

        for (Posicion punto : ruta.obtenerPuntos()) {
            if (!contiene(punto)) {
                throw new IllegalArgumentException("Todos los puntos de la ruta deben estar dentro del escenario");
            }
        }

        for (Slot slot : slots) {
            if (!contiene(slot.obtenerPosicion())) {
                throw new IllegalArgumentException("Todos los slots deben estar dentro del escenario");
            }
        }
    }

    private void validarCoherenciaEntreBasesYRuta() {
        Posicion inicio = ruta.obtenerInicio();
        Posicion fin = ruta.obtenerFin();

        if (!mismaPosicion(inicio, baseEnemiga.obtenerPosicion())) {
            throw new IllegalArgumentException("La ruta debe iniciar en la base enemiga");
        }

        if (!mismaPosicion(fin, baseJugador.obtenerPosicion())) {
            throw new IllegalArgumentException("La ruta debe finalizar en la base del jugador");
        }
    }

    private boolean mismaPosicion(Posicion una, Posicion otra) {
        return una.obtenerX() == otra.obtenerX() && una.obtenerY() == otra.obtenerY();
    }
}
