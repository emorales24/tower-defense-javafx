package Vista;

import Modelo.Juego;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class BarraJuego {
    private static final double ALTO = 48;
    private static final double TAMANIO_BOTON_ICONO = 34;
    private static final double TAMANIO_ICONO_ESTADO = 24;
    private static final double TAMANIO_ICONO_BASE = 28;
    private static final double ANCHO_BARRA_BASE = 130;
    private static final double ALTO_BARRA_BASE = 16;
    private static final String ESTILO_TEXTO = "-fx-text-fill: white; -fx-font-weight: bold;";
    private static final String ESTILO_VIDA_BASE = "-fx-accent: #d63b32; -fx-control-inner-background: #3a1717; -fx-background-radius: 8;";
    private static final String ESTILO_TEXTO_VIDA_BASE = "-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(one-pass-box, #101010, 2, 1, 0, 1);";
    private static final String ESTILO_BOTON_ICONO = "-fx-background-color: #111a22; -fx-background-radius: 18; -fx-border-color: #314455; -fx-border-radius: 18;";
    private static final String ESTILO_BOTON_ICONO_HOVER = "-fx-background-color: #1b2a36; -fx-background-radius: 18; -fx-border-color: #557087; -fx-border-radius: 18;";
    private static final Color COLOR_ICONO = Color.web("#e8edf0");
    private static final Color COLOR_ICONO_MUTEADO = Color.web("#8f9aa3");

    private final HBox raiz;
    private final AssetsJuego assets;
    private final Label estado;
    private final Label dinero;
    private final Label puntaje;
    private final Label mensaje;
    private final ProgressBar vidaBase;
    private final Label porcentajeVidaBase;
    private final Button botonAudio;
    private final Button botonPausa;

    public BarraJuego(Runnable alAlternarAudio, Runnable alAlternarPausa) {
        if (alAlternarAudio == null || alAlternarPausa == null) {
            throw new IllegalArgumentException("Las acciones de la barra no pueden ser null");
        }

        this.assets = new AssetsJuego();
        this.estado = new Label();
        this.dinero = new Label();
        this.puntaje = new Label();
        this.mensaje = new Label();
        this.vidaBase = new ProgressBar(1);
        this.porcentajeVidaBase = new Label("100%");
        this.botonAudio = crearBotonIcono("Mutear audio", crearIconoAudio(false), alAlternarAudio);
        this.botonPausa = crearBotonIcono("Pausar juego", crearIconoPausa(), alAlternarPausa);
        this.raiz = crearRaiz();
    }

    public HBox obtenerRaiz() {
        return raiz;
    }

    public double obtenerAlto() {
        return ALTO;
    }

    public void actualizar(Juego juego) {
        estado.setText("Nivel " + (juego.obtenerIndiceNivelActual() + 1));
        dinero.setText("$" + juego.obtenerJugador().obtenerDineroDisponible());
        puntaje.setText("Pts " + juego.obtenerJugador().obtenerPuntajeAcumulado());
        actualizarVidaBase(juego);
    }

    public void mostrarMensaje(String texto) {
        mensaje.setText(texto);
    }

    public void actualizarAudio(boolean muteado) {
        botonAudio.setTooltip(new Tooltip(muteado ? "Activar audio" : "Mutear audio"));
        botonAudio.setGraphic(crearIconoAudio(muteado));
    }

    public void actualizarPausa(boolean pausado) {
        botonPausa.setTooltip(new Tooltip(pausado ? "Menu de pausa" : "Pausar juego"));
    }

    private HBox crearRaiz() {
        HBox barra = new HBox(10);
        barra.setMinHeight(ALTO);
        barra.setPrefHeight(ALTO);
        barra.setStyle("-fx-padding: 10; -fx-background-color: #20242a; -fx-text-fill: white;");

        estado.setTextFill(Color.WHITE);
        dinero.setTextFill(Color.WHITE);
        puntaje.setTextFill(Color.WHITE);
        mensaje.setTextFill(Color.WHITE);
        estado.setStyle(ESTILO_TEXTO);
        dinero.setStyle(ESTILO_TEXTO);
        puntaje.setStyle(ESTILO_TEXTO);
        mensaje.setStyle(ESTILO_TEXTO);
        porcentajeVidaBase.setStyle(ESTILO_TEXTO_VIDA_BASE);
        vidaBase.setMinSize(ANCHO_BARRA_BASE, ALTO_BARRA_BASE);
        vidaBase.setPrefSize(ANCHO_BARRA_BASE, ALTO_BARRA_BASE);
        vidaBase.setMaxSize(ANCHO_BARRA_BASE, ALTO_BARRA_BASE);
        vidaBase.setStyle(ESTILO_VIDA_BASE);

        Region espacioFlexible = new Region();
        HBox.setHgrow(espacioFlexible, Priority.ALWAYS);

        barra.getChildren().addAll(
                estado,
                crearIndicador(assets.obtenerMonedas(), dinero, "Dinero disponible"),
                crearIndicador(assets.obtenerPuntaje(), puntaje, "Puntaje acumulado"),
                crearIndicadorVidaBase(),
                mensaje,
                espacioFlexible,
                botonPausa,
                botonAudio
        );

        return barra;
    }

    private HBox crearIndicador(Image imagen, Label valor, String descripcion) {
        ImageView icono = new ImageView(imagen);
        icono.setFitWidth(TAMANIO_ICONO_ESTADO);
        icono.setFitHeight(TAMANIO_ICONO_ESTADO);
        icono.setPreserveRatio(true);

        HBox indicador = new HBox(4, icono, valor);
        indicador.setAlignment(Pos.CENTER);
        Tooltip.install(indicador, new Tooltip(descripcion));
        return indicador;
    }

    private HBox crearIndicadorVidaBase() {
        ImageView icono = new ImageView(assets.obtenerBaseJugador());
        icono.setFitWidth(TAMANIO_ICONO_BASE);
        icono.setFitHeight(TAMANIO_ICONO_BASE);
        icono.setPreserveRatio(true);

        StackPane barraConTexto = new StackPane(vidaBase, porcentajeVidaBase);
        HBox indicador = new HBox(6, icono, barraConTexto);
        indicador.setAlignment(Pos.CENTER);
        Tooltip.install(indicador, new Tooltip("Vida de la base"));
        return indicador;
    }

    private void actualizarVidaBase(Juego juego) {
        double porcentaje = juego.obtenerNivelActual().obtenerEscenario().obtenerBaseJugador().obtenerPorcentajeVida();
        vidaBase.setProgress(porcentaje);
        porcentajeVidaBase.setText(Math.round(porcentaje * 100) + "%");
    }

    private Button crearBotonIcono(String descripcion, StackPane icono, Runnable accion) {
        Button boton = new Button();
        boton.setFocusTraversable(false);
        boton.setTooltip(new Tooltip(descripcion));
        boton.setMinSize(TAMANIO_BOTON_ICONO, TAMANIO_BOTON_ICONO);
        boton.setPrefSize(TAMANIO_BOTON_ICONO, TAMANIO_BOTON_ICONO);
        boton.setMaxSize(TAMANIO_BOTON_ICONO, TAMANIO_BOTON_ICONO);
        boton.setGraphic(icono);
        boton.setStyle(ESTILO_BOTON_ICONO);
        boton.setOnMouseEntered(evento -> boton.setStyle(ESTILO_BOTON_ICONO_HOVER));
        boton.setOnMouseExited(evento -> boton.setStyle(ESTILO_BOTON_ICONO));
        boton.setOnAction(evento -> accion.run());
        return boton;
    }

    private StackPane crearIconoPausa() {
        Rectangle barraIzquierda = new Rectangle(4, 14, COLOR_ICONO);
        Rectangle barraDerecha = new Rectangle(4, 14, COLOR_ICONO);
        barraIzquierda.setTranslateX(-4);
        barraDerecha.setTranslateX(4);
        return new StackPane(barraIzquierda, barraDerecha);
    }

    private StackPane crearIconoAudio(boolean muteado) {
        Color color = muteado ? COLOR_ICONO_MUTEADO : COLOR_ICONO;
        Rectangle caja = new Rectangle(5, 8, color);
        caja.setTranslateX(-6);

        Polygon cono = new Polygon(
                -4.0, -5.0,
                3.0, -10.0,
                3.0, 10.0,
                -4.0, 5.0
        );
        cono.setFill(color);

        Line ondaUno = crearLineaIcono(7, -6, 10, 0, color);
        Line ondaDos = crearLineaIcono(10, 0, 7, 6, color);
        StackPane icono = new StackPane(caja, cono, ondaUno, ondaDos);

        if (muteado) {
            Line mute = crearLineaIcono(10, -9, -10, 9, COLOR_ICONO);
            mute.setStrokeWidth(2.5);
            icono.getChildren().add(mute);
        }

        return icono;
    }

    private Line crearLineaIcono(double inicioX, double inicioY, double finX, double finY, Color color) {
        Line linea = new Line(inicioX, inicioY, finX, finY);
        linea.setStroke(color);
        linea.setStrokeWidth(2);
        return linea;
    }
}
