package Vista;

import Modelo.Juego;
import Modelo.Daniable;
import Modelo.enemigo.Enemigo;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.nivel.EventoEnemigoEliminado;
import Modelo.nivel.Nivel;
import Modelo.proyectil.Proyectil;
import Modelo.torreta.Torreta;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class RenderizadorJuego {
    private static final double TAMANIO_TILE = 32;
    private static final double TAMANIO_BASE = 42;
    private static final double TAMANIO_TORRETA = 32;
    private static final double TAMANIO_ENEMIGO = 32;
    private static final double TAMANIO_PROYECTIL = 22;
    private static final double ANCHO_BARRA_VIDA = 34;
    private static final double ALTO_BARRA_VIDA = 5;

    private final Canvas canvas;
    private final AssetsJuego assets;
    private final List<AnimacionExplosion> explosiones;

    public RenderizadorJuego(Canvas canvas) {
        if (canvas == null) {
            throw new IllegalArgumentException("El canvas no puede ser null");
        }

        this.canvas = canvas;
        this.assets = new AssetsJuego();
        this.explosiones = new ArrayList<>();
    }

    public void actualizar(double deltaTiempo) {
        for (AnimacionExplosion explosion : explosiones) {
            explosion.actualizar(deltaTiempo);
        }

        explosiones.removeIf(AnimacionExplosion::finalizo);
    }

    public void registrarExplosion(EventoEnemigoEliminado evento) {
        explosiones.add(new AnimacionExplosion(evento.obtenerPosicion(), evento.obtenerTipoEnemigo(), assets));
    }

    public void dibujar(Juego juego, Slot slotResaltado) {
        GraphicsContext graficos = canvas.getGraphicsContext2D();
        graficos.setImageSmoothing(false);
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        Nivel nivel = juego.obtenerNivelActual();

        dibujarFondo(graficos);

        dibujarRuta(graficos, escenario.obtenerRuta());
        dibujarSlots(graficos, escenario, nivel, slotResaltado);
        dibujarBase(graficos, escenario);
        dibujarTorretas(graficos, nivel);
        dibujarEnemigos(graficos, nivel);
        dibujarProyectiles(graficos, nivel);
        dibujarExplosiones(graficos);
    }

    private void dibujarFondo(GraphicsContext graficos) {
        for (int x = 0; x < canvas.getWidth(); x += TAMANIO_TILE) {
            for (int y = 0; y < canvas.getHeight(); y += TAMANIO_TILE) {
                graficos.drawImage(assets.obtenerPiso(x, y), x, y, TAMANIO_TILE, TAMANIO_TILE);
            }
        }
    }

    private void dibujarRuta(GraphicsContext graficos, Ruta ruta) {
        for (int i = 0; i < ruta.cantidadPuntos() - 1; i++) {
            Posicion inicio = ruta.obtenerPunto(i);
            Posicion fin = ruta.obtenerPunto(i + 1);
            dibujarTramoRuta(graficos, inicio, fin, ruta.obtenerAncho());
        }
    }

    private void dibujarTramoRuta(GraphicsContext graficos, Posicion inicio, Posicion fin, double ancho) {
        double distancia = inicio.distanciaA(fin);
        if (distancia == 0) {
            dibujarCentrado(graficos, assets.obtenerRuta(), inicio, ancho, ancho);
            return;
        }

        double pasoX = (fin.obtenerX() - inicio.obtenerX()) / distancia;
        double pasoY = (fin.obtenerY() - inicio.obtenerY()) / distancia;

        for (double recorrido = 0; recorrido < distancia; recorrido += ancho) {
            Posicion posicion = new Posicion(
                    inicio.obtenerX() + pasoX * recorrido,
                    inicio.obtenerY() + pasoY * recorrido
            );
            dibujarCentrado(graficos, assets.obtenerRuta(), posicion, ancho, ancho);
        }

        dibujarCentrado(graficos, assets.obtenerRuta(), fin, ancho, ancho);
    }

    private void dibujarSlots(GraphicsContext graficos, Escenario escenario, Nivel nivel, Slot slotResaltado) {
        for (Slot slot : escenario.obtenerSlots()) {
            double ancho = slot.obtenerDimension().obtenerAncho();
            double alto = slot.obtenerDimension().obtenerAlto();
            double x = slot.obtenerPosicion().obtenerX() - ancho / 2;
            double y = slot.obtenerPosicion().obtenerY() - alto / 2;
            boolean ocupado = nivel.obtenerTorretasInstaladas().containsKey(slot);

            if (!ocupado) {
                graficos.drawImage(assets.obtenerSlot(), x, y, ancho, alto);
            }

            graficos.setStroke(slot.equals(slotResaltado) ? Color.web("#f5f0a6") : Color.web("#6b5425"));
            graficos.setLineWidth(slot.equals(slotResaltado) ? 4 : 2);
            graficos.strokeRect(x, y, ancho, alto);
        }
    }

    private void dibujarBase(GraphicsContext graficos, Escenario escenario) {
        Posicion posicion = escenario.obtenerBaseJugador().obtenerPosicion();
        double x = posicion.obtenerX() - TAMANIO_BASE / 2;
        double y = posicion.obtenerY() - TAMANIO_BASE / 2;

        dibujarCentrado(graficos, assets.obtenerBaseJugador(), posicion, TAMANIO_BASE, TAMANIO_BASE);
        dibujarBarraVida(graficos, escenario.obtenerBaseJugador(), posicion.obtenerX() - ANCHO_BARRA_VIDA / 2, y - 10);
    }

    private void dibujarTorretas(GraphicsContext graficos, Nivel nivel) {
        for (Torreta torreta : nivel.obtenerTorretasInstaladas().values()) {
            dibujarCentrado(graficos, assets.obtenerTorreta(torreta.obtenerTipo()), torreta.obtenerPosicion(), TAMANIO_TORRETA, TAMANIO_TORRETA);
        }
    }

    private void dibujarEnemigos(GraphicsContext graficos, Nivel nivel) {
        for (Enemigo enemigo : nivel.obtenerEnemigosActivos()) {
            Posicion posicion = enemigo.obtenerPosicion();
            double x = posicion.obtenerX() - TAMANIO_ENEMIGO / 2;
            double y = posicion.obtenerY() - TAMANIO_ENEMIGO / 2;

            dibujarCentrado(graficos, assets.obtenerEnemigo(enemigo.obtenerTipo()), posicion, TAMANIO_ENEMIGO, TAMANIO_ENEMIGO);
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

    private void dibujarProyectiles(GraphicsContext graficos, Nivel nivel) {
        for (Proyectil proyectil : nivel.obtenerProyectilesActivos()) {
            Image imagen = assets.obtenerProyectil(proyectil.obtenerTipoTorreta(), direccionProyectil(proyectil));
            dibujarCentrado(graficos, imagen, proyectil.obtenerPosicion(), TAMANIO_PROYECTIL, TAMANIO_PROYECTIL);
        }
    }

    private void dibujarExplosiones(GraphicsContext graficos) {
        for (AnimacionExplosion explosion : explosiones) {
            explosion.dibujar(graficos);
        }
    }

    private int direccionProyectil(Proyectil proyectil) {
        if (proyectil.obtenerObjetivo() == null) {
            return 2;
        }

        Posicion origen = proyectil.obtenerPosicion();
        Posicion destino = proyectil.obtenerObjetivo().obtenerPosicion();
        double angulo = Math.atan2(destino.obtenerY() - origen.obtenerY(), destino.obtenerX() - origen.obtenerX());
        return (int) Math.round((angulo + Math.PI / 2) / (Math.PI / 4));
    }

    private void dibujarCentrado(GraphicsContext graficos, Image imagen, Posicion posicion, double ancho, double alto) {
        graficos.drawImage(
                imagen,
                posicion.obtenerX() - ancho / 2,
                posicion.obtenerY() - alto / 2,
                ancho,
                alto
        );
    }
}
