package Modelo.torreta;

import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;

public class TorretaSimple extends Torreta {
    private static final double COSTO = 50;
    private static final double DANIO = 1;
    private static final double ALCANCE = 150;
    private static final double INTERVALO_ENTRE_DISPAROS = 1.0;
    private static final double VELOCIDAD_PROYECTIL = 250;
    private static final Dimension DIMENSION = new Dimension(32, 32);
    private static final Dimension DIMENSION_PROYECTIL = new Dimension(8, 8);

    public TorretaSimple(Posicion posicion) {
        super(
                TipoTorreta.SIMPLE,
                posicion,
                COSTO,
                DANIO,
                ALCANCE,
                INTERVALO_ENTRE_DISPAROS,
                VELOCIDAD_PROYECTIL,
                DIMENSION,
                DIMENSION_PROYECTIL
        );
    }
}