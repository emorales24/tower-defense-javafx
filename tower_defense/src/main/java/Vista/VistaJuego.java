package Vista;

import Modelo.EstadoJuego;
import Modelo.Juego;
import Modelo.escena.Escenario;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.torreta.TipoTorreta;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;

import java.util.function.IntConsumer;

public class VistaJuego {
    private static final int ACTUALIZACIONES_POR_SEGUNDO = 60;
    private static final double SEGUNDOS_POR_ACTUALIZACION = 1.0 / ACTUALIZACIONES_POR_SEGUNDO;
    private static final long NANOSEGUNDOS_POR_ACTUALIZACION = 1_000_000_000L / ACTUALIZACIONES_POR_SEGUNDO;

    private final Juego juego;
    private final BorderPane raiz;
    private final Canvas canvas;
    private final BarraJuego barra;
    private final RenderizadorJuego renderizador;
    private final AudioJuego audio;
    private final IntConsumer alGanarNivel;
    private final Runnable alPerder;
    private final Runnable alGanarJuego;
    private final Runnable alVolverAlMenu;
    private TipoTorreta tipoSeleccionado;
    private Slot slotResaltado;
    private int ultimoNivelMostrado;
    private AnimationTimer loopActualizacion;
    private AnimationTimer loopRender;

    public VistaJuego(Juego juego, AudioJuego audio, IntConsumer alGanarNivel, Runnable alPerder, Runnable alGanarJuego, Runnable alVolverAlMenu) {
        if (juego == null || audio == null || alGanarNivel == null || alPerder == null || alGanarJuego == null || alVolverAlMenu == null) {
            throw new IllegalArgumentException("La vista de juego requiere juego y acciones de finalizacion");
        }

        this.juego = juego;
        this.audio = audio;
        this.alGanarNivel = alGanarNivel;
        this.alPerder = alPerder;
        this.alGanarJuego = alGanarJuego;
        this.alVolverAlMenu = alVolverAlMenu;
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        this.canvas = new Canvas(escenario.obtenerAncho(), escenario.obtenerAlto());
        this.tipoSeleccionado = TipoTorreta.SIMPLE;
        this.slotResaltado = null;
        this.ultimoNivelMostrado = juego.obtenerIndiceNivelActual();
        this.barra = new BarraJuego(this::seleccionarTorreta, this::alternarAudio, this::reiniciarNivelActual, this::volverAlMenu);
        this.renderizador = new RenderizadorJuego(canvas);
        this.raiz = crearRaiz();
        configurarClicks();
    }

    public BorderPane obtenerRaiz() {
        return raiz;
    }

    public double obtenerAncho() {
        return canvas.getWidth();
    }

    public double obtenerAlto() {
        return canvas.getHeight() + barra.obtenerAlto();
    }

    public void iniciar() {
        juego.iniciar();
        dibujar();
        iniciarLoops();
    }

    private BorderPane crearRaiz() {
        BorderPane contenedor = new BorderPane();
        contenedor.setTop(barra.obtenerRaiz());
        contenedor.setCenter(canvas);
        return contenedor;
    }

    private void seleccionarTorreta(TipoTorreta tipo) {
        tipoSeleccionado = tipo;
        barra.actualizar(juego);
    }

    private void reiniciarNivelActual() {
        juego.obtenerNivelActual().iniciar();
        slotResaltado = null;
        barra.mostrarMensaje("Nivel reiniciado");
        dibujar();
    }

    private void alternarAudio() {
        audio.alternarMute();
        barra.actualizarAudio(audio.estaMuteado());
    }

    private void volverAlMenu() {
        detenerLoop();
        alVolverAlMenu.run();
    }

    private void configurarClicks() {
        canvas.setOnMouseMoved(evento -> {
            Slot nuevoSlotResaltado = buscarSlotEn(evento.getX(), evento.getY());
            if (nuevoSlotResaltado != slotResaltado) {
                slotResaltado = nuevoSlotResaltado;
                dibujar();
            }
        });

        canvas.setOnMouseExited(evento -> {
            slotResaltado = null;
            dibujar();
        });

        canvas.setOnMouseClicked(evento -> {
            if (juego.obtenerEstado() != EstadoJuego.EN_CURSO) {
                return;
            }

            Slot slot = buscarSlotEn(evento.getX(), evento.getY());
            if (slot == null) {
                barra.mostrarMensaje("Selecciona un slot");
                return;
            }

            boolean estabaOcupado = juego.obtenerNivelActual().obtenerTorretasInstaladas().containsKey(slot);
            boolean instalada = juego.obtenerNivelActual().instalarTorreta(slot, tipoSeleccionado);
            if (instalada && estabaOcupado) {
                barra.mostrarMensaje("Torreta reemplazada");
            } else if (instalada) {
                barra.mostrarMensaje("Torreta instalada");
            } else {
                barra.mostrarMensaje("Dinero insuficiente");
            }
            dibujar();
        });
    }

    private Slot buscarSlotEn(double x, double y) {
        Posicion posicionClick = new Posicion(x, y);

        for (Slot slot : juego.obtenerNivelActual().obtenerEscenario().obtenerSlots()) {
            if (slot.contiene(posicionClick)) {
                return slot;
            }
        }

        return null;
    }

    private void iniciarLoops() {
        loopActualizacion = new AnimationTimer() {
            private long ultimoTiempo = 0;
            private long tiempoAcumulado = 0;

            @Override
            public void handle(long ahora) {
                if (ultimoTiempo == 0) {
                    ultimoTiempo = ahora;
                    return;
                }

                tiempoAcumulado += ahora - ultimoTiempo;
                ultimoTiempo = ahora;

                while (tiempoAcumulado >= NANOSEGUNDOS_POR_ACTUALIZACION) {
                    tiempoAcumulado -= NANOSEGUNDOS_POR_ACTUALIZACION;
                    if (!actualizarJuego()) {
                        tiempoAcumulado = 0;
                        return;
                    }
                }
            }
        };

        loopRender = new AnimationTimer() {
            @Override
            public void handle(long ahora) {
                dibujar();
            }
        };

        loopActualizacion.start();
        loopRender.start();
    }

    private void detenerLoop() {
        if (loopActualizacion != null) {
            loopActualizacion.stop();
        }

        if (loopRender != null) {
            loopRender.stop();
        }
    }

    private boolean actualizarJuego() {
        juego.actualizar(SEGUNDOS_POR_ACTUALIZACION);
        juego.obtenerNivelActual().consumirEventosEnemigosEliminados().forEach(evento -> {
            audio.reproducirExplosion(evento.obtenerTipoEnemigo());
            renderizador.registrarExplosion(evento);
        });
        renderizador.actualizar(SEGUNDOS_POR_ACTUALIZACION);

        if (juego.obtenerEstado() == EstadoJuego.VICTORIA_NIVEL) {
            detenerLoop();
            Platform.runLater(this::continuarDespuesDeVictoriaDeNivel);
            return false;
        } else if (juego.obtenerEstado() != EstadoJuego.EN_CURSO) {
            detenerLoop();
            Platform.runLater(this::notificarEstadoFinal);
            return false;
        }

        return true;
    }

    private void continuarDespuesDeVictoriaDeNivel() {
        int nivelSiguiente = juego.obtenerIndiceNivelActual() + 2;
        alGanarNivel.accept(nivelSiguiente);

        juego.avanzarAlSiguienteNivel();
        ultimoNivelMostrado = juego.obtenerIndiceNivelActual();
        slotResaltado = null;
        ajustarCanvasAlNivelActual();
        barra.mostrarMensaje("Nivel " + (ultimoNivelMostrado + 1));
        dibujar();
        iniciarLoops();
    }

    private void ajustarCanvasAlNivelActual() {
        Escenario escenario = juego.obtenerNivelActual().obtenerEscenario();
        canvas.setWidth(escenario.obtenerAncho());
        canvas.setHeight(escenario.obtenerAlto());
    }

    private void notificarEstadoFinal() {
        if (juego.obtenerEstado() == EstadoJuego.DERROTA) {
            alPerder.run();
        } else if (juego.obtenerEstado() == EstadoJuego.VICTORIA_FINAL) {
            alGanarJuego.run();
        }
    }

    private void dibujar() {
        barra.actualizar(juego);
        renderizador.dibujar(juego, slotResaltado);
    }
}
