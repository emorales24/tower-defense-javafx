package Vista;

import Modelo.EstadoJuego;
import Modelo.Juego;
import Modelo.escena.Escenario;
import Modelo.escena.Slot;
import Modelo.geometria.Posicion;
import Modelo.torreta.TipoTorreta;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.function.IntConsumer;

public class VistaJuego {
    private static final int ACTUALIZACIONES_POR_SEGUNDO = 60;
    private static final double SEGUNDOS_POR_ACTUALIZACION = 1.0 / ACTUALIZACIONES_POR_SEGUNDO;
    private static final long NANOSEGUNDOS_POR_ACTUALIZACION = 1_000_000_000L / ACTUALIZACIONES_POR_SEGUNDO;
    private static final String ESTILO_MODAL_PAUSA = "-fx-background-color: #111a22; -fx-border-color: #314455; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;";
    private static final String ESTILO_TITULO_PAUSA = "-fx-text-fill: #f3f0df; -fx-font-size: 24; -fx-font-weight: bold;";
    private static final String ESTILO_TEXTO_MODAL = "-fx-text-fill: #dce6ec; -fx-font-size: 13; -fx-font-weight: bold;";
    private static final String ESTILO_BOTON_PAUSA = "-fx-background-color: #243342; -fx-text-fill: #f4f0df; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-color: #557087; -fx-border-radius: 6;";
    private static final String ESTILO_BOTON_PAUSA_HOVER = "-fx-background-color: #33485a; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-color: #f5f0a6; -fx-border-radius: 6;";
    private static final ColorAdjust FILTRO_FONDO_MODAL = new ColorAdjust(0, -0.35, -0.55, 0);

    private final Juego juego;
    private final BorderPane raiz;
    private final Canvas canvas;
    private final BarraJuego barra;
    private final SelectorTorretas selectorTorretas;
    private final RenderizadorJuego renderizador;
    private final AudioJuego audio;
    private final IntConsumer alGanarNivel;
    private final Runnable alPerder;
    private final Runnable alGanarJuego;
    private final Runnable alVolverAlMenu;
    private TipoTorreta tipoSeleccionado;
    private Slot slotResaltado;
    private int ultimoNivelMostrado;
    private boolean pausado;
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
        this.pausado = false;
        this.barra = new BarraJuego(this::alternarAudio, this::mostrarMenuPausa);
        this.selectorTorretas = new SelectorTorretas(this::seleccionarTorreta);
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
        return canvas.getHeight() + barra.obtenerAlto() + selectorTorretas.obtenerAlto();
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
        contenedor.setBottom(selectorTorretas.obtenerRaiz());
        return contenedor;
    }

    private void seleccionarTorreta(TipoTorreta tipo) {
        tipoSeleccionado = tipo;
        selectorTorretas.seleccionar(tipo);
        barra.actualizar(juego);
        dibujar();
    }

    private void reiniciarNivelActual() {
        juego.reiniciarNivelActual();
        slotResaltado = null;
        pausado = false;
        barra.actualizarPausa(pausado);
        barra.mostrarMensaje("Nivel reiniciado");
        dibujar();
    }

    private void alternarAudio() {
        audio.alternarMute();
        barra.actualizarAudio(audio.estaMuteado());
    }

    private void mostrarMenuPausa() {
        pausarJuego();
        Stage modal = crearModalPausa();
        mostrarModalConFiltro(modal);
    }

    private void pausarJuego() {
        pausado = true;
        barra.actualizarPausa(true);
        barra.mostrarMensaje("Juego pausado");
        dibujar();
    }

    private void reanudarJuego() {
        pausado = false;
        barra.actualizarPausa(false);
        barra.mostrarMensaje("");
        dibujar();
    }

    private Stage crearModalPausa() {
        Stage modal = new Stage();
        modal.setTitle("Pausa");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);

        Window ventana = raiz.getScene() == null ? null : raiz.getScene().getWindow();
        if (ventana != null) {
            modal.initOwner(ventana);
        }

        Label titulo = new Label("Pausa");
        titulo.setStyle(ESTILO_TITULO_PAUSA);

        Button resumir = crearBotonModal("Resumir");
        resumir.setOnAction(evento -> {
            reanudarJuego();
            modal.close();
        });

        Button reiniciar = crearBotonModal("Reiniciar");
        reiniciar.setOnAction(evento -> {
            reiniciarNivelActual();
            modal.close();
        });

        Button menu = crearBotonModal("Menu");
        menu.setOnAction(evento -> {
            modal.close();
            volverAlMenu();
        });

        VBox contenido = new VBox(14, titulo, crearIconoPausaModal(), resumir, reiniciar, menu);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(24));
        contenido.setStyle(ESTILO_MODAL_PAUSA);

        modal.setScene(new Scene(contenido, 250, 315));
        modal.setResizable(false);
        modal.setOnCloseRequest(evento -> reanudarJuego());
        return modal;
    }

    private Button crearBotonModal(String texto) {
        Button boton = new Button(texto);
        boton.setFocusTraversable(false);
        boton.setMinWidth(150);
        boton.setPrefHeight(38);
        boton.setStyle(ESTILO_BOTON_PAUSA);
        boton.setOnMouseEntered(evento -> boton.setStyle(ESTILO_BOTON_PAUSA_HOVER));
        boton.setOnMouseExited(evento -> boton.setStyle(ESTILO_BOTON_PAUSA));
        return boton;
    }

    private void mostrarModalConFiltro(Stage modal) {
        raiz.setEffect(FILTRO_FONDO_MODAL);
        modal.showAndWait();
        raiz.setEffect(null);
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
            if (pausado || juego.obtenerEstado() != EstadoJuego.EN_CURSO) {
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
                if (pausado) {
                    ultimoTiempo = ahora;
                    tiempoAcumulado = 0;
                    return;
                }

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

        boolean continuar = mostrarModalVictoriaNivel(nivelSiguiente);
        if (!continuar) {
            volverAlMenu();
            return;
        }

        juego.avanzarAlSiguienteNivel();
        ultimoNivelMostrado = juego.obtenerIndiceNivelActual();
        slotResaltado = null;
        pausado = false;
        barra.actualizarPausa(pausado);
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
            mostrarModalDerrota();
        } else if (juego.obtenerEstado() == EstadoJuego.VICTORIA_FINAL) {
            alGanarJuego.run();
            mostrarModalVictoriaFinal();
        }
    }

    private boolean mostrarModalVictoriaNivel(int nivelSiguiente) {
        boolean[] continuar = {false};

        Button siguienteNivel = crearBotonModal("Siguiente nivel");
        siguienteNivel.setOnAction(evento -> {
            continuar[0] = true;
            ((Stage) siguienteNivel.getScene().getWindow()).close();
        });

        Button menu = crearBotonModal("Menu");
        menu.setOnAction(evento -> ((Stage) menu.getScene().getWindow()).close());

        Stage modal = crearModalResultado(
                "Nivel completado",
                "Sigue el nivel " + nivelSiguiente,
                crearIconoVictoriaNivel(),
                siguienteNivel,
                menu
        );
        mostrarModalConFiltro(modal);
        return continuar[0];
    }

    private void mostrarModalDerrota() {
        Button reiniciar = crearBotonModal("Reiniciar");
        reiniciar.setOnAction(evento -> {
            ((Stage) reiniciar.getScene().getWindow()).close();
            reiniciarNivelActual();
            iniciarLoops();
        });

        Button menu = crearBotonModal("Menu");
        menu.setOnAction(evento -> {
            ((Stage) menu.getScene().getWindow()).close();
            volverAlMenu();
        });

        Stage modal = crearModalResultado(
                "Derrota",
                "La base fue destruida",
                crearIconoDerrota(),
                reiniciar,
                menu
        );
        mostrarModalConFiltro(modal);
    }

    private void mostrarModalVictoriaFinal() {
        Button menu = crearBotonModal("Menu");
        menu.setOnAction(evento -> {
            ((Stage) menu.getScene().getWindow()).close();
            volverAlMenu();
        });

        Stage modal = crearModalResultado(
                "Victoria final",
                "Felicitaciones, ganaste el juego",
                crearIconoVictoriaFinal(),
                menu
        );
        mostrarModalConFiltro(modal);
    }

    private Stage crearModalResultado(String titulo, String mensaje, Pane icono, Button... botones) {
        Stage modal = new Stage();
        modal.setTitle(titulo);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.UNDECORATED);

        Window ventana = raiz.getScene() == null ? null : raiz.getScene().getWindow();
        if (ventana != null) {
            modal.initOwner(ventana);
        }

        Label tituloModal = new Label(titulo);
        tituloModal.setStyle(ESTILO_TITULO_PAUSA);

        Label mensajeModal = new Label(mensaje);
        mensajeModal.setStyle(ESTILO_TEXTO_MODAL);

        HBox acciones = new HBox(10, botones);
        acciones.setAlignment(Pos.CENTER);

        VBox contenido = new VBox(16, tituloModal, icono, mensajeModal, acciones);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(26));
        contenido.setStyle(ESTILO_MODAL_PAUSA);

        modal.setScene(new Scene(contenido, 330, 300));
        modal.setResizable(false);
        return modal;
    }

    private Pane crearIconoDerrota() {
        StackPane icono = new StackPane();
        icono.setPrefSize(94, 86);

        Circle fondo = new Circle(36, Color.web("#431d22"));
        fondo.setStroke(Color.web("#c95a5f"));
        fondo.setStrokeWidth(3);

        Polygon escudo = new Polygon(
                0.0, -28.0,
                24.0, -16.0,
                18.0, 22.0,
                0.0, 32.0,
                -18.0, 22.0,
                -24.0, -16.0
        );
        escudo.setFill(Color.web("#7c2930"));
        escudo.setStroke(Color.web("#f0b3ad"));
        escudo.setStrokeWidth(2);

        Line cruzUno = crearLineaIcono(-13, -8, 13, 18, Color.web("#f0d2c9"));
        Line cruzDos = crearLineaIcono(13, -8, -13, 18, Color.web("#f0d2c9"));
        cruzUno.setStrokeWidth(4);
        cruzDos.setStrokeWidth(4);

        icono.getChildren().addAll(fondo, escudo, cruzUno, cruzDos);
        animarPulso(icono);
        return icono;
    }

    private Pane crearIconoPausaModal() {
        StackPane icono = new StackPane();
        icono.setPrefSize(82, 72);

        Circle fondo = new Circle(30, Color.web("#142230"));
        fondo.setStroke(Color.web("#557087"));
        fondo.setStrokeWidth(3);

        Rectangle barraIzquierda = new Rectangle(8, 30, Color.web("#dce6ec"));
        Rectangle barraDerecha = new Rectangle(8, 30, Color.web("#dce6ec"));
        barraIzquierda.setArcWidth(4);
        barraIzquierda.setArcHeight(4);
        barraDerecha.setArcWidth(4);
        barraDerecha.setArcHeight(4);
        barraIzquierda.setTranslateX(-9);
        barraDerecha.setTranslateX(9);

        icono.getChildren().addAll(fondo, barraIzquierda, barraDerecha);
        animarPulso(icono);
        return icono;
    }

    private Pane crearIconoVictoriaNivel() {
        StackPane icono = new StackPane();
        icono.setPrefSize(94, 86);

        Circle fondo = new Circle(36, Color.web("#173626"));
        fondo.setStroke(Color.web("#4bd27a"));
        fondo.setStrokeWidth(3);

        Polygon flecha = new Polygon(
                -22.0, -10.0,
                7.0, -10.0,
                7.0, -24.0,
                28.0, 0.0,
                7.0, 24.0,
                7.0, 10.0,
                -22.0, 10.0
        );
        flecha.setFill(Color.web("#4bd27a"));

        icono.getChildren().addAll(fondo, flecha);
        animarPulso(icono);
        return icono;
    }

    private Pane crearIconoVictoriaFinal() {
        StackPane icono = new StackPane();
        icono.setPrefSize(94, 86);

        Circle fondo = new Circle(36, Color.web("#3f3212"));
        fondo.setStroke(Color.web("#f5c84b"));
        fondo.setStrokeWidth(3);

        Rectangle copa = new Rectangle(34, 26, Color.web("#f5c84b"));
        copa.setArcWidth(10);
        copa.setArcHeight(10);
        copa.setTranslateY(-12);

        Rectangle pie = new Rectangle(10, 22, Color.web("#d69d24"));
        pie.setTranslateY(10);
        Rectangle base = new Rectangle(44, 8, Color.web("#f5c84b"));
        base.setArcWidth(6);
        base.setArcHeight(6);
        base.setTranslateY(26);

        Circle brillo = new Circle(5, Color.web("#fff2a6"));
        brillo.setTranslateX(20);
        brillo.setTranslateY(-25);

        icono.getChildren().addAll(fondo, copa, pie, base, brillo);
        animarPulso(icono);

        RotateTransition rotacion = new RotateTransition(Duration.seconds(1.8), brillo);
        rotacion.setByAngle(360);
        rotacion.setCycleCount(RotateTransition.INDEFINITE);
        rotacion.play();
        return icono;
    }

    private Line crearLineaIcono(double inicioX, double inicioY, double finX, double finY, Color color) {
        Line linea = new Line(inicioX, inicioY, finX, finY);
        linea.setStroke(color);
        linea.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        return linea;
    }

    private void animarPulso(Pane nodo) {
        ScaleTransition escala = new ScaleTransition(Duration.seconds(0.85), nodo);
        escala.setFromX(0.96);
        escala.setFromY(0.96);
        escala.setToX(1.06);
        escala.setToY(1.06);
        escala.setAutoReverse(true);
        escala.setCycleCount(ScaleTransition.INDEFINITE);
        escala.play();

        FadeTransition brillo = new FadeTransition(Duration.seconds(0.85), nodo);
        brillo.setFromValue(0.82);
        brillo.setToValue(1);
        brillo.setAutoReverse(true);
        brillo.setCycleCount(FadeTransition.INDEFINITE);
        brillo.play();
    }

    private void dibujar() {
        barra.actualizar(juego);
        selectorTorretas.actualizarDisponibilidad(juego.obtenerJugador().obtenerDineroDisponible());
        renderizador.dibujar(juego, slotResaltado, tipoSeleccionado);
    }
}
