package Modelo.geometria;

public class Dimension {
    private final double ancho;
    private final double alto;

    public Dimension(double ancho, double alto) {
        if (Double.isNaN(ancho) || Double.isInfinite(ancho) || ancho <= 0
                || Double.isNaN(alto) || Double.isInfinite(alto) || alto <= 0) {
            throw new IllegalArgumentException("Las dimensiones deben ser positivas y finitas");
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
