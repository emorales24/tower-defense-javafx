package Vista;

import Modelo.EstadoJuego;
import Modelo.Juego;
import Modelo.enemigo.Enemigo;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.nivel.Nivel;
import Modelo.proyectil.Proyectil;
import Modelo.torreta.Torreta;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class VistaJuego {
    private static final double ALTO_BARRA = 48;
    private static final double TAMANIO_BASE = 36;
    private static final double TAMANIO_ENEMIGO = 24;
    private static final double TAMANIO_PROYECTIL = 8;

    private final Juego juego;
    private final BorderPane raiz;
    private final Canvas canvas;
    private final Label estado;
    private final Label dinero;
    private final Label puntaje;

    public VistaJuego(Juego juego) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego no puede ser null");
        }

        this.juego = juego;
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        this.canvas = new Canvas(escenario.obtenerAncho(), escenario.obtenerAlto());
        this.estado = new Label();
        this.dinero = new Label();
        this.puntaje = new Label();
        this.raiz = crearRaiz();
    }

    public BorderPane obtenerRaiz() {
        return raiz;
    }

    public double obtenerAncho() {
        return canvas.getWidth();
    }

    public double obtenerAlto() {
        return canvas.getHeight() + ALTO_BARRA;
    }

    public void iniciar() {
        juego.iniciar();
        dibujar();
        iniciarLoop();
    }

    private BorderPane crearRaiz() {
        HBox barra = new HBox(24, estado, dinero, puntaje);
        barra.setMinHeight(ALTO_BARRA);
        barra.setPrefHeight(ALTO_BARRA);
        barra.setStyle("-fx-padding: 12; -fx-background-color: #20242a; -fx-text-fill: white;");
        estado.setTextFill(Color.WHITE);
        dinero.setTextFill(Color.WHITE);
        puntaje.setTextFill(Color.WHITE);

        BorderPane contenedor = new BorderPane();
        contenedor.setTop(barra);
        contenedor.setCenter(canvas);
        return contenedor;
    }

    private void iniciarLoop() {
        AnimationTimer loop = new AnimationTimer() {
            private long ultimoFrame = 0;

            @Override
            public void handle(long ahora) {
                if (ultimoFrame == 0) {
                    ultimoFrame = ahora;
                    return;
                }

                double deltaTiempo = (ahora - ultimoFrame) / 1_000_000_000.0;
                ultimoFrame = ahora;

                juego.actualizar(deltaTiempo);
                dibujar();

                if (juego.obtenerEstado() != EstadoJuego.EN_CURSO) {
                    stop();
                }
            }
        };
        loop.start();
    }

    private void dibujar() {
        actualizarBarra();

        GraphicsContext graficos = canvas.getGraphicsContext2D();
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        Nivel nivel = juego.obtenerNivelActual();

        graficos.setFill(Color.web("#eef2e6"));
        graficos.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        dibujarRuta(graficos, escenario.obtenerRuta());
        dibujarSlots(graficos, escenario);
        dibujarBase(graficos, escenario);
        dibujarTorretas(graficos, nivel);
        dibujarEnemigos(graficos, nivel);
        dibujarProyectiles(graficos, nivel);
    }

    private void actualizarBarra() {
        estado.setText("Estado: " + juego.obtenerEstado() + " | Nivel " + (juego.obtenerIndiceNivelActual() + 1));
        dinero.setText("Dinero: " + juego.obtenerJugador().obtenerDineroDisponible());
        puntaje.setText("Puntaje: " + juego.obtenerJugador().obtenerPuntajeAcumulado());
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

    private void dibujarSlots(GraphicsContext graficos, Escenario escenario) {
        for (Slot slot : escenario.obtenerSlots()) {
            double ancho = slot.obtenerDimension().obtenerAncho();
            double alto = slot.obtenerDimension().obtenerAlto();
            double x = slot.obtenerPosicion().obtenerX() - ancho / 2;
            double y = slot.obtenerPosicion().obtenerY() - alto / 2;

            graficos.setFill(Color.web("#d4b16a"));
            graficos.fillRect(x, y, ancho, alto);
            graficos.setStroke(Color.web("#6b5425"));
            graficos.setLineWidth(2);
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
        }
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
