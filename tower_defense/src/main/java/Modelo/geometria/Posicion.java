package Modelo.geometria;

public class Posicion {
    private final double x;
    private final double y;

    public Posicion(double x, double y) {
        if (Double.isNaN(x) || Double.isInfinite(x) || Double.isNaN(y) || Double.isInfinite(y)) {
            throw new IllegalArgumentException("Las coordenadas deben ser finitas");
        }

        this.x = x;
        this.y = y;
    }

    public double obtenerX() {
        return x;
    }

    public double obtenerY() {
        return y;
    }

    public double distanciaA(Posicion otra) {
        if (otra == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        double diferenciaX = otra.x - this.x;
        double diferenciaY = otra.y - this.y;

        return Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
    }

    /**
     * Mueve hacia destino la posición actual en la distancia dada
     */
    public Posicion moverHacia(Posicion destino, double distancia) {
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser null");
        }

        if (Double.isNaN(distancia) || Double.isInfinite(distancia) || distancia < 0) {
            throw new IllegalArgumentException("La distancia debe ser no negativa y finita");
        }

        double distanciaAlDestino = distanciaA(destino);

        if (distanciaAlDestino == 0 || distancia >= distanciaAlDestino) {
            return destino;
        }

        double direccionX = (destino.x - this.x) / distanciaAlDestino;
        double direccionY = (destino.y - this.y) / distanciaAlDestino;

        return new Posicion(
                this.x + direccionX * distancia,
                this.y + direccionY * distancia
        );
    }

    public boolean estaCercaDe(Posicion otra, double tolerancia) {
        if (otra == null) {
            throw new IllegalArgumentException("La posicion no puede ser null");
        }

        if (Double.isNaN(tolerancia) || Double.isInfinite(tolerancia) || tolerancia < 0) {
            throw new IllegalArgumentException("La tolerancia debe ser no negativa y finita");
        }

        return distanciaA(otra) <= tolerancia;
    }
}
