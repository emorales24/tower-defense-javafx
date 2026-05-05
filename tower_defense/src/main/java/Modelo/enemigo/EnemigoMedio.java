package Modelo.enemigo;

import Modelo.escena.Ruta;
import Modelo.geometria.Dimension;

public class EnemigoMedio extends Enemigo {
    private static final double VIDA_MAXIMA = 8;
    private static final double VELOCIDAD = 90;
    private static final double RECOMPENSA_DINERO = 20;
    private static final double RECOMPENSA_PUNTAJE = 20;
    private static final double DANIO_A_BASE = 5;
    private static final Dimension DIMENSION = new Dimension(32, 32);

    public EnemigoMedio(Ruta ruta) {
        super(
                TipoEnemigo.MEDIO,
                ruta,
                VIDA_MAXIMA,
                VELOCIDAD,
                RECOMPENSA_DINERO,
                RECOMPENSA_PUNTAJE,
                DANIO_A_BASE,
                DIMENSION
        );
    }
}