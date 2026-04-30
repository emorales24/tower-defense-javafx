package Carga;

public class CargaNivelException extends RuntimeException {
    public CargaNivelException(String mensaje) {
        super(mensaje);
    }

    public CargaNivelException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
