package Vista;

import Modelo.Juego;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class BarraJuego {
    private static final double ALTO = 48;
    private static final double ANCHO_BOTON_AUDIO = 58;
    private static final double ANCHO_BOTON_PAUSA = 78;
    private static final double ANCHO_BOTON_REINICIAR = 82;
    private static final double ANCHO_BOTON_MENU = 62;
    private static final double TAMANIO_ICONO_ESTADO = 24;
    private static final String ESTILO_BOTON_MUTED = "-fx-background-color: #6d7380; -fx-text-fill: white;";
    private static final String ESTILO_TEXTO = "-fx-text-fill: white; -fx-font-weight: bold;";

    private final HBox raiz;
    private final AssetsJuego assets;
    private final Label estado;
    private final Label dinero;
    private final Label puntaje;
    private final Label mensaje;
    private final Button botonAudio;
    private final Button botonPausa;
    private boolean audioMuteado;

    public BarraJuego(Runnable alAlternarAudio, Runnable alAlternarPausa, Runnable alReiniciarNivel, Runnable alVolverAlMenu) {
        if (alAlternarAudio == null || alAlternarPausa == null || alReiniciarNivel == null || alVolverAlMenu == null) {
            throw new IllegalArgumentException("Las acciones de la barra no pueden ser null");
        }

        this.assets = new AssetsJuego();
        this.estado = new Label();
        this.dinero = new Label();
        this.puntaje = new Label();
        this.mensaje = new Label();
        this.botonAudio = crearBotonAccion("Audio", "Mutear audio", ANCHO_BOTON_AUDIO, alAlternarAudio);
        this.botonPausa = crearBotonAccion("Pausa", "Pausar juego", ANCHO_BOTON_PAUSA, alAlternarPausa);
        this.audioMuteado = false;
        this.raiz = crearRaiz(alReiniciarNivel, alVolverAlMenu);
        actualizarBotones();
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
    }

    public void mostrarMensaje(String texto) {
        mensaje.setText(texto);
    }

    public void actualizarAudio(boolean muteado) {
        audioMuteado = muteado;
        botonAudio.setText(muteado ? "Mute" : "Audio");
        botonAudio.setTooltip(new Tooltip(muteado ? "Activar audio" : "Mutear audio"));
    }

    public void actualizarPausa(boolean pausado) {
        botonPausa.setText(pausado ? "Reanudar" : "Pausa");
        botonPausa.setTooltip(new Tooltip(pausado ? "Reanudar juego" : "Pausar juego"));
    }

    private HBox crearRaiz(Runnable alReiniciarNivel, Runnable alVolverAlMenu) {
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

        Region espacioFlexible = new Region();
        HBox.setHgrow(espacioFlexible, Priority.ALWAYS);

        barra.getChildren().addAll(
                estado,
                crearIndicador(assets.obtenerMonedas(), dinero, "Dinero disponible"),
                crearIndicador(assets.obtenerPuntaje(), puntaje, "Puntaje acumulado"),
                mensaje,
                espacioFlexible,
                botonPausa,
                botonAudio,
                crearBotonAccion("Reiniciar", "Reiniciar nivel", ANCHO_BOTON_REINICIAR, alReiniciarNivel),
                crearBotonAccion("Menu", "Volver al menu", ANCHO_BOTON_MENU, alVolverAlMenu)
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

    private Button crearBotonAccion(String texto, String descripcion, double ancho, Runnable accion) {
        Button boton = new Button(texto);
        boton.setFocusTraversable(false);
        boton.setTooltip(new Tooltip(descripcion));
        boton.setMinWidth(ancho);
        boton.setPrefWidth(ancho);
        boton.setOnAction(evento -> accion.run());
        return boton;
    }

    private void actualizarBotones() {
        if (audioMuteado) {
            botonAudio.setStyle(ESTILO_BOTON_MUTED);
        } else {
            botonAudio.setStyle("");
        }
    }
}
