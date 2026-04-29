package Modelo.enemigo;

import Modelo.escena.Ruta;
import Modelo.geometria.Dimension;

public class EnemigoFuerte extends Enemigo {
    private static final double VIDA_MAXIMA = 3;
    private static final double VELOCIDAD = 50;
    private static final double RECOMPENSA_DINERO = 30;
    private static final double RECOMPENSA_PUNTAJE = 30;
    private static final double DANIO_A_BASE = 3;
    private static final Dimension DIMENSION = new Dimension(32, 32);

    public EnemigoFuerte(Ruta ruta) {
        super(
                TipoEnemigo.FUERTE,
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