package Carga;

import Modelo.Juego;

import java.util.List;

public interface CargadorJuego {
    Juego cargar(List<String> rutasNiveles);
}
