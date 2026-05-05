package Carga;

import Modelo.Jugador;
import Modelo.nivel.Nivel;

public interface CargadorNivel {
    Nivel cargar(String rutaArchivo, Jugador jugador);
}
