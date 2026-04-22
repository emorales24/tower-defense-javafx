package Modelo.geometria;

public class Posicion {
    private final double x;
    private final double y;

    public Posicion(double x, double y) {
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
        double diferenciaX = otra.x - this.x;
        double diferenciaY = otra.y - this.y;

        return Math.sqrt(diferenciaX * diferenciaX + diferenciaY * diferenciaY);
    }

    /**
     * Mueve hacia destino la posición actual en la distancia dada
     */
    public Posicion moverHacia(Posicion destino, double distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa");
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
        if (tolerancia < 0) {
            throw new IllegalArgumentException("La tolerancia no puede ser negativa");
        }

        return distanciaA(otra) <= tolerancia;
    }
}
