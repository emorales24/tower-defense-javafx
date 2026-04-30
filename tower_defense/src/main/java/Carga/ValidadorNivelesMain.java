package Carga;

import Modelo.Jugador;

import java.util.List;

public class ValidadorNivelesMain {
    public static void main(String[] args) {
        CargadorNivel cargador = new CargadorNivelXml();
        Jugador jugador = new Jugador();
        List<String> niveles = args.length > 0
                ? List.of(args)
                : List.of("niveles/nivel1.xml", "niveles/nivel2.xml", "niveles/nivel3.xml");

        try {
            for (String ruta : niveles) {
                cargador.cargar(ruta, jugador);
            }
        } catch (CargaNivelException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

