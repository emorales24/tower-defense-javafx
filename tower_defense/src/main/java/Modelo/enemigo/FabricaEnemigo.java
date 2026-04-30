package Modelo.enemigo;

import Modelo.escena.Ruta;

public class FabricaEnemigo {
    public Enemigo crear(TipoEnemigo tipoEnemigo, Ruta ruta) {
        if (tipoEnemigo == null || ruta == null) {
            throw new IllegalArgumentException("La fabrica requiere tipo de enemigo y ruta");
        }

        switch (tipoEnemigo) {
            case DEBIL:
                return new EnemigoDebil(ruta);
            case MEDIO:
                return new EnemigoMedio(ruta);
            case FUERTE:
                return new EnemigoFuerte(ruta);
            default:
                throw new IllegalStateException("Tipo de enemigo no soportado");
        }
    }
}
