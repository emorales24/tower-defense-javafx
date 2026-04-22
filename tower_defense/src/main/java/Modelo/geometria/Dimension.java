package Modelo.geometria;

public class Dimension {
    private final double ancho;
    private final double alto;

    public Dimension(double ancho, double alto) {
        if (ancho <= 0 || alto <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser positivas");
        }

        this.ancho = ancho;
        this.alto = alto;
    }

    public double obtenerAncho() {
        return ancho;
    }

    public double obtenerAlto() {
        return alto;
    }
}
