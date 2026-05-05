package Modelo;

public interface Actualizable {
    void actualizar(double deltaTiempo);

    static void validarDeltaTiempo(double deltaTiempo) {
        if (Double.isNaN(deltaTiempo) || Double.isInfinite(deltaTiempo)) {
            throw new IllegalArgumentException("El delta de tiempo debe ser finito");
        }

        if (deltaTiempo < 0) {
            throw new IllegalArgumentException("El delta de tiempo no puede ser negativo");
        }
    }
}
