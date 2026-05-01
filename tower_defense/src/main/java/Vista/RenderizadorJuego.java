package Vista;

import Modelo.Juego;
import Modelo.Daniable;
import Modelo.enemigo.Enemigo;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.nivel.Nivel;
import Modelo.proyectil.Proyectil;
import Modelo.torreta.Torreta;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class RenderizadorJuego {
    private static final double TAMANIO_BASE = 36;
    private static final double TAMANIO_ENEMIGO = 24;
    private static final double TAMANIO_PROYECTIL = 8;
    private static final double ANCHO_BARRA_VIDA = 34;
    private static final double ALTO_BARRA_VIDA = 5;

    private final Canvas canvas;

    public RenderizadorJuego(Canvas canvas) {
        if (canvas == null) {
            throw new IllegalArgumentException("El canvas no puede ser null");
        }

        this.canvas = canvas;
    }

    public void dibujar(Juego juego, Slot slotResaltado) {
        GraphicsContext graficos = canvas.getGraphicsContext2D();
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        Nivel nivel = juego.obtenerNivelActual();

        graficos.setFill(Color.web("#eef2e6"));
        graficos.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        dibujarRuta(graficos, escenario.obtenerRuta());
        dibujarSlots(graficos, escenario, nivel, slotResaltado);
        dibujarBase(graficos, escenario);
        dibujarTorretas(graficos, nivel);
        dibujarEnemigos(graficos, nivel);
        dibujarProyectiles(graficos, nivel);
    }

    private void dibujarRuta(GraphicsContext graficos, Ruta ruta) {
        graficos.setStroke(Color.web("#7c8a74"));
        graficos.setLineWidth(ruta.obtenerAncho());
        graficos.setLineCap(StrokeLineCap.ROUND);

        for (int i = 0; i < ruta.cantidadPuntos() - 1; i++) {
            Posicion inicio = ruta.obtenerPunto(i);
            Posicion fin = ruta.obtenerPunto(i + 1);
            graficos.strokeLine(inicio.obtenerX(), inicio.obtenerY(), fin.obtenerX(), fin.obtenerY());
        }
    }

    private void dibujarSlots(GraphicsContext graficos, Escenario escenario, Nivel nivel, Slot slotResaltado) {
        for (Slot slot : escenario.obtenerSlots()) {
            double ancho = slot.obtenerDimension().obtenerAncho();
            double alto = slot.obtenerDimension().obtenerAlto();
            double x = slot.obtenerPosicion().obtenerX() - ancho / 2;
            double y = slot.obtenerPosicion().obtenerY() - alto / 2;
            boolean ocupado = nivel.obtenerTorretasInstaladas().containsKey(slot);

            graficos.setFill(ocupado ? Color.web("#7aa66a") : Color.web("#d4b16a"));
            graficos.fillRect(x, y, ancho, alto);
            graficos.setStroke(slot.equals(slotResaltado) ? Color.web("#f5f0a6") : Color.web("#6b5425"));
            graficos.setLineWidth(slot.equals(slotResaltado) ? 4 : 2);
            graficos.strokeRect(x, y, ancho, alto);
        }
    }

    private void dibujarBase(GraphicsContext graficos, Escenario escenario) {
        Posicion posicion = escenario.obtenerBaseJugador().obtenerPosicion();
        double x = posicion.obtenerX() - TAMANIO_BASE / 2;
        double y = posicion.obtenerY() - TAMANIO_BASE / 2;

        graficos.setFill(Color.web("#5478c9"));
        graficos.fillRect(x, y, TAMANIO_BASE, TAMANIO_BASE);
        graficos.setStroke(Color.web("#1d3975"));
        graficos.setLineWidth(2);
        graficos.strokeRect(x, y, TAMANIO_BASE, TAMANIO_BASE);
        dibujarBarraVida(graficos, escenario.obtenerBaseJugador(), posicion.obtenerX() - ANCHO_BARRA_VIDA / 2, y - 10);
    }

    private void dibujarTorretas(GraphicsContext graficos, Nivel nivel) {
        for (Torreta torreta : nivel.obtenerTorretasInstaladas().values()) {
            Posicion posicion = torreta.obtenerPosicion();
            double radio = 14;

            graficos.setFill(Color.web("#2f6f4e"));
            graficos.fillOval(posicion.obtenerX() - radio, posicion.obtenerY() - radio, radio * 2, radio * 2);
            graficos.setStroke(Color.web("#163b28"));
            graficos.setLineWidth(2);
            graficos.strokeOval(posicion.obtenerX() - radio, posicion.obtenerY() - radio, radio * 2, radio * 2);
        }
    }

    private void dibujarEnemigos(GraphicsContext graficos, Nivel nivel) {
        for (Enemigo enemigo : nivel.obtenerEnemigosActivos()) {
            Posicion posicion = enemigo.obtenerPosicion();
            double x = posicion.obtenerX() - TAMANIO_ENEMIGO / 2;
            double y = posicion.obtenerY() - TAMANIO_ENEMIGO / 2;

            graficos.setFill(colorEnemigo(enemigo));
            graficos.fillOval(x, y, TAMANIO_ENEMIGO, TAMANIO_ENEMIGO);
            graficos.setStroke(Color.web("#3d1b1b"));
            graficos.setLineWidth(2);
            graficos.strokeOval(x, y, TAMANIO_ENEMIGO, TAMANIO_ENEMIGO);
            dibujarBarraVida(graficos, enemigo, posicion.obtenerX() - ANCHO_BARRA_VIDA / 2, y - 9);
        }
    }

    private void dibujarBarraVida(GraphicsContext graficos, Daniable daniable, double x, double y) {
        graficos.setFill(Color.web("#2b2b2b"));
        graficos.fillRect(x, y, ANCHO_BARRA_VIDA, ALTO_BARRA_VIDA);

        graficos.setFill(Color.web("#4caf50"));
        graficos.fillRect(x, y, ANCHO_BARRA_VIDA * daniable.obtenerPorcentajeVida(), ALTO_BARRA_VIDA);

        graficos.setStroke(Color.web("#101010"));
        graficos.setLineWidth(1);
        graficos.strokeRect(x, y, ANCHO_BARRA_VIDA, ALTO_BARRA_VIDA);
    }

    private Color colorEnemigo(Enemigo enemigo) {
        switch (enemigo.obtenerTipo()) {
            case MEDIO:
                return Color.web("#c5533d");
            case FUERTE:
                return Color.web("#8e2f3f");
            case DEBIL:
            default:
                return Color.web("#df8f45");
        }
    }

    private void dibujarProyectiles(GraphicsContext graficos, Nivel nivel) {
        graficos.setFill(Color.web("#111111"));
        for (Proyectil proyectil : nivel.obtenerProyectilesActivos()) {
            Posicion posicion = proyectil.obtenerPosicion();
            graficos.fillOval(
                    posicion.obtenerX() - TAMANIO_PROYECTIL / 2,
                    posicion.obtenerY() - TAMANIO_PROYECTIL / 2,
                    TAMANIO_PROYECTIL,
                    TAMANIO_PROYECTIL
            );
        }
    }
}
