package Vista;

import Modelo.enemigo.TipoEnemigo;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioJuego {
    private MediaPlayer musicaActual;
    private AudioClip musicaActualClip;
    private String rutaMusicaActualWav;
    private String rutaMusicaActualMp3;
    private boolean muteado;
    private boolean musicaDisponible;
    private boolean efectosDisponibles;

    public AudioJuego() {
        this.muteado = false;
        this.musicaDisponible = true;
        this.efectosDisponibles = true;
    }

    public void reproducirMusicaMenu() {
        reproducirMusica("/assets/menu/song18.wav", "/assets/menu/song18.mp3");
    }

    public void reproducirMusicaJuego() {
        reproducirMusica("/assets/escenario/Orbital Colossus.wav", "/assets/escenario/Orbital Colossus.mp3");
    }

    public void detenerMusica() {
        if (musicaActualClip != null) {
            musicaActualClip.stop();
            musicaActualClip = null;
        }

        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.dispose();
            musicaActual = null;
        }
    }

    public boolean estaMuteado() {
        return muteado;
    }

    public void alternarMute() {
        muteado = !muteado;

        if (muteado) {
            detenerReproduccionActual();
        } else if (rutaMusicaActualWav != null) {
            reproducirMusica(rutaMusicaActualWav, rutaMusicaActualMp3);
        }
    }

    public void reproducirExplosion(TipoEnemigo tipoEnemigo) {
        if (muteado || !efectosDisponibles) {
            return;
        }

        if (tipoEnemigo == TipoEnemigo.FUERTE) {
            reproducirEfecto("/assets/enemigos/explosion_enemigo_fuerte.wav");
        } else {
            reproducirEfecto("/assets/enemigos/explosion_enemigos_1y2.wav");
        }
    }

    private void reproducirMusica(String rutaWav, String rutaMp3) {
        detenerReproduccionActual();
        rutaMusicaActualWav = rutaWav;
        rutaMusicaActualMp3 = rutaMp3;

        if (muteado || !musicaDisponible) {
            return;
        }

        if (existe(rutaWav) && reproducirMusicaConAudioClip(rutaWav)) {
            return;
        }

        if (!existe(rutaMp3)) {
            musicaDisponible = false;
            return;
        }

        try {
            MediaPlayer reproductor = new MediaPlayer(new Media(obtenerUrl(rutaMp3)));
            reproductor.setCycleCount(MediaPlayer.INDEFINITE);
            reproductor.setVolume(0.35);
            reproductor.setMute(muteado);
            reproductor.setOnError(() -> {
                musicaDisponible = false;
                detenerMusica();
            });
            reproductor.play();
            musicaActual = reproductor;
        } catch (IllegalArgumentException | MediaException e) {
            musicaActual = null;
            musicaDisponible = false;
        }
    }

    private void detenerReproduccionActual() {
        if (musicaActualClip != null) {
            musicaActualClip.stop();
            musicaActualClip = null;
        }

        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.dispose();
            musicaActual = null;
        }
    }

    private boolean reproducirMusicaConAudioClip(String ruta) {
        try {
            AudioClip musica = new AudioClip(obtenerUrl(ruta));
            musica.setCycleCount(AudioClip.INDEFINITE);
            musica.setVolume(muteado ? 0 : 0.35);
            musica.play();
            musicaActualClip = musica;
            return true;
        } catch (IllegalArgumentException | MediaException e) {
            musicaActualClip = null;
            return false;
        }
    }

    private void reproducirEfecto(String ruta) {
        try {
            AudioClip efecto = new AudioClip(obtenerUrl(ruta));
            efecto.setVolume(0.8);
            efecto.play();
        } catch (IllegalArgumentException | MediaException e) {
            efectosDisponibles = false;
        }
    }

    private String obtenerUrl(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) {
            throw new IllegalArgumentException("No se encontro el audio " + ruta);
        }

        return url.toExternalForm();
    }

    private boolean existe(String ruta) {
        return getClass().getResource(ruta) != null;
    }
}
