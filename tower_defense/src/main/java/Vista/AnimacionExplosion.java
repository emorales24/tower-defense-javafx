package Vista;

import Modelo.enemigo.TipoEnemigo;
import Modelo.geometria.Posicion;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimacionExplosion {
    private static final double DURACION_FRAME = 0.12;
    private static final double TAMANIO = 44;

    private final Posicion posicion;
    private final Image[] frames;
    private double tiempoTranscurrido;

    public AnimacionExplosion(Posicion posicion, TipoEnemigo tipoEnemigo, AssetsJuego assets) {
        if (posicion == null || tipoEnemigo == null || assets == null) {
            throw new IllegalArgumentException("La animacion requiere posicion, tipo de enemigo y assets");
        }

        this.posicion = posicion;
        this.frames = assets.obtenerExplosion(tipoEnemigo);
        this.tiempoTranscurrido = 0;
    }

    public void actualizar(double deltaTiempo) {
        tiempoTranscurrido += deltaTiempo;
    }

    public boolean finalizo() {
        return indiceFrameActual() >= frames.length;
    }

    public void dibujar(GraphicsContext graficos) {
        if (finalizo()) {
            return;
        }

        Image frame = frames[indiceFrameActual()];
        graficos.drawImage(
                frame,
                posicion.obtenerX() - TAMANIO / 2,
                posicion.obtenerY() - TAMANIO / 2,
                TAMANIO,
                TAMANIO
        );
    }

    private int indiceFrameActual() {
        return (int) (tiempoTranscurrido / DURACION_FRAME);
    }
}
