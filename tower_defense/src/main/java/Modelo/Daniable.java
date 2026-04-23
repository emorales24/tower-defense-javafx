package Modelo;

public interface Daniable {
    void recibirDanio(double cantidadDanio);

    double obtenerVidaActual();

    double obtenerVidaMaxima();

    default boolean estaDestruido() {
        return obtenerVidaActual() <= 0;
    }

    default boolean estaVivo() {
        return !estaDestruido();
    }

    default double obtenerPorcentajeVida() {
        if (obtenerVidaMaxima() <= 0) {
            throw new IllegalStateException("La vida maxima debe ser positiva");
        }

        double porcentajeVida = obtenerVidaActual() / obtenerVidaMaxima();
        return Math.max(0, Math.min(1, porcentajeVida));
    }

    static void validarDanio(double cantidadDanio) {
        if (Double.isNaN(cantidadDanio) || Double.isInfinite(cantidadDanio)) {
            throw new IllegalArgumentException("La cantidad de danio debe ser finita");
        }

        if (cantidadDanio < 0) {
            throw new IllegalArgumentException("La cantidad de danio no puede ser negativa");
        }
    }
}
