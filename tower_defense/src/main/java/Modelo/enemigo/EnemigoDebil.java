package Modelo.enemigo;

import Modelo.escena.Ruta;
import Modelo.geometria.Dimension;

public class EnemigoDebil extends Enemigo {
    private static final double VIDA_MAXIMA = 3;
    private static final double VELOCIDAD = 130;
    private static final double RECOMPENSA_DINERO = 8;
    private static final double RECOMPENSA_PUNTAJE = 8;
    private static final double DANIO_A_BASE = 2;
    private static final Dimension DIMENSION = new Dimension(32, 32);

    public EnemigoDebil(Ruta ruta) {
        super(
                TipoEnemigo.DEBIL,
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