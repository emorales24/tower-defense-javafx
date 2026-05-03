package Modelo.enemigo;

import Modelo.escena.Ruta;
import Modelo.geometria.Dimension;

public class EnemigoFuerte extends Enemigo {
    private static final double VIDA_MAXIMA = 20;
    private static final double VELOCIDAD = 60;
    private static final double RECOMPENSA_DINERO = 35;
    private static final double RECOMPENSA_PUNTAJE = 35;
    private static final double DANIO_A_BASE = 12;
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