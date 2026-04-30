package Carga;

import Modelo.Juego;
import Modelo.Jugador;
import Modelo.nivel.Nivel;

import java.util.ArrayList;
import java.util.List;

public class CargadorJuegoXml implements CargadorJuego {
    private static final List<String> RUTAS_NIVELES_POR_DEFECTO = List.of(
            "niveles/nivel1.xml",
            "niveles/nivel2.xml",
            "niveles/nivel3.xml"
    );

    private final CargadorNivel cargadorNivel;

    public CargadorJuegoXml() {
        this(new CargadorNivelXml());
    }

    public CargadorJuegoXml(CargadorNivel cargadorNivel) {
        if (cargadorNivel == null) {
            throw new IllegalArgumentException("El cargador de nivel no puede ser null");
        }

        this.cargadorNivel = cargadorNivel;
    }

    public Juego cargar() {
        return cargar(RUTAS_NIVELES_POR_DEFECTO);
    }

    @Override
    public Juego cargar(List<String> rutasNiveles) {
        if (rutasNiveles == null || rutasNiveles.isEmpty()) {
            throw new IllegalArgumentException("Debe indicarse al menos un nivel");
        }

        Jugador jugador = new Jugador();
        List<Nivel> niveles = new ArrayList<>();

        for (String ruta : rutasNiveles) {
            niveles.add(cargadorNivel.cargar(ruta, jugador));
        }

        return new Juego(jugador, niveles);
    }
}
