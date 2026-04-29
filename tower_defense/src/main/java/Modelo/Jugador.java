package Modelo;

public class Jugador {
    private double dineroDisponible;
    private double puntajeAcumulado;

    public Jugador() {
        this(0, 0);
    }

    public Jugador(double dineroDisponible, double puntajeAcumulado) {
        validarValorNoNegativoYFinito(dineroDisponible, "El dinero disponible debe ser no negativo y finito");
        validarValorNoNegativoYFinito(puntajeAcumulado, "El puntaje acumulado debe ser no negativo y finito");

        this.dineroDisponible = dineroDisponible;
        this.puntajeAcumulado = puntajeAcumulado;
    }

    public double obtenerDineroDisponible() {
        return dineroDisponible;
    }

    public double obtenerPuntajeAcumulado() {
        return puntajeAcumulado;
    }

    public boolean puedeComprar(double costo) {
        validarValorNoNegativoYFinito(costo, "El costo debe ser no negativo y finito");
        return dineroDisponible >= costo;
    }

    public void descontarDinero(double costo) {
        validarValorNoNegativoYFinito(costo, "El costo debe ser no negativo y finito");

        if (!puedeComprar(costo)) {
            throw new IllegalStateException("No hay dinero suficiente para realizar la compra");
        }

        dineroDisponible -= costo;
    }

    public void sumarDinero(double cantidad) {
        validarValorNoNegativoYFinito(cantidad, "La cantidad de dinero debe ser no negativa y finita");
        dineroDisponible += cantidad;
    }

    public void sumarPuntaje(double cantidad) {
        validarValorNoNegativoYFinito(cantidad, "La cantidad de puntaje debe ser no negativa y finita");
        puntajeAcumulado += cantidad;
    }

    public void establecerDineroDisponible(double dineroDisponible) {
        validarValorNoNegativoYFinito(dineroDisponible, "El dinero disponible debe ser no negativo y finito");
        this.dineroDisponible = dineroDisponible;
    }

    private static void validarValorNoNegativoYFinito(double valor, String mensaje) {
        if (Double.isNaN(valor) || Double.isInfinite(valor) || valor < 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
