package Modelo.escena;

import Modelo.geometria.Posicion;

import java.util.List;

public class Escenario {
    private final double ancho;
    private final double alto;
    private final Ruta ruta;
    private final Posicion puntoSpawn;
    private final Base baseJugador;
    private final List<Slot> slots;

    public Escenario(double ancho, double alto, Ruta ruta, Posicion puntoSpawn, Base baseJugador, List<Slot> slots) {
        if (Double.isNaN(ancho) || Double.isInfinite(ancho) || ancho <= 0) {
            throw new IllegalArgumentException("El ancho debe ser positivo y finito");
        }

        if (Double.isNaN(alto) || Double.isInfinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("El alto debe ser positivo y finito");
        }

        if (ruta == null || puntoSpawn == null || baseJugador == null || slots == null) {
            throw new IllegalArgumentException("Los componentes del escenario no pueden ser null");
        }

        this.ancho = ancho;
        this.alto = alto;
        this.ruta = ruta;
        this.puntoSpawn = puntoSpawn;
        this.baseJugador = baseJugador;
        this.slots = List.copyOf(slots);

        validarElementosDentroDelEscenario();
        validarCoherenciaEntreSpawnYRuta();
        validarSlotsFueraDeRuta();
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

    public Posicion obtenerPuntoSpawn() {
        return puntoSpawn;
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
        if (!contiene(puntoSpawn) || !contiene(baseJugador.obtenerPosicion())) {
            throw new IllegalArgumentException("El punto de spawn y la base del jugador deben estar dentro del escenario");
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

    private void validarCoherenciaEntreSpawnYRuta() {
        Posicion inicio = ruta.obtenerInicio();
        Posicion fin = ruta.obtenerFin();

        if (!mismaPosicion(inicio, puntoSpawn)) {
            throw new IllegalArgumentException("La ruta debe iniciar en el punto de spawn");
        }

        if (!mismaPosicion(fin, baseJugador.obtenerPosicion())) {
            throw new IllegalArgumentException("La ruta debe finalizar en la base del jugador");
        }
    }

    private boolean mismaPosicion(Posicion una, Posicion otra) {
        return una.obtenerX() == otra.obtenerX() && una.obtenerY() == otra.obtenerY();
    }

    private void validarSlotsFueraDeRuta() {
        for (Slot slot : slots) {
            if (slotColisionaConRuta(slot)) {
                throw new IllegalArgumentException("Los slots no pueden colisionar con la ruta");
            }
        }
    }

    private boolean slotColisionaConRuta(Slot slot) {
        List<Posicion> puntos = ruta.obtenerPuntos();
        double distanciaMinima = ruta.obtenerAncho() / 2 + obtenerMediaDiagonal(slot);

        for (int i = 0; i < puntos.size() - 1; i++) {
            double distancia = distanciaDePuntoASegmento(
                    slot.obtenerPosicion(),
                    puntos.get(i),
                    puntos.get(i + 1)
            );

            if (distancia <= distanciaMinima) {
                return true;
            }
        }

        return false;
    }

    private double obtenerMediaDiagonal(Slot slot) {
        double medioAncho = slot.obtenerDimension().obtenerAncho() / 2;
        double medioAlto = slot.obtenerDimension().obtenerAlto() / 2;
        return Math.sqrt(medioAncho * medioAncho + medioAlto * medioAlto);
    }

    private double distanciaDePuntoASegmento(Posicion punto, Posicion inicio, Posicion fin) {
        double segmentoX = fin.obtenerX() - inicio.obtenerX();
        double segmentoY = fin.obtenerY() - inicio.obtenerY();
        double largoSegmentoCuadrado = segmentoX * segmentoX + segmentoY * segmentoY;

        if (largoSegmentoCuadrado == 0) {
            return punto.distanciaA(inicio);
        }

        double proporcion = (
                (punto.obtenerX() - inicio.obtenerX()) * segmentoX
                        + (punto.obtenerY() - inicio.obtenerY()) * segmentoY
        ) / largoSegmentoCuadrado;

        double proporcionAcotada = Math.max(0, Math.min(1, proporcion));
        Posicion puntoMasCercano = new Posicion(
                inicio.obtenerX() + proporcionAcotada * segmentoX,
                inicio.obtenerY() + proporcionAcotada * segmentoY
        );

        return punto.distanciaA(puntoMasCercano);
    }
}
