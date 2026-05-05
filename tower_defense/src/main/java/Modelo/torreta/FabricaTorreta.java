package Modelo.torreta;

import Modelo.geometria.Posicion;

public class FabricaTorreta {
    public Torreta crear(TipoTorreta tipoTorreta, Posicion posicion) {
        if (tipoTorreta == null || posicion == null) {
            throw new IllegalArgumentException("La fabrica requiere tipo de torreta y posicion");
        }

        switch (tipoTorreta) {
            case SIMPLE:
                return new TorretaSimple(posicion);
            case RAPIDA:
                return new TorretaRapida(posicion);
            case POTENTE:
                return new TorretaPotente(posicion);
            case DISTANTE:
                return new TorretaDistante(posicion);
            default:
                throw new IllegalStateException("Tipo de torreta no soportado");
        }
    }
}
