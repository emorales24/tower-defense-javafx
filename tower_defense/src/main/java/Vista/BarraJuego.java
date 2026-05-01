package Vista;

import Modelo.Juego;
import Modelo.torreta.TipoTorreta;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class BarraJuego {
    private static final double ALTO = 48;
    private static final double ANCHO_BOTON_TORRETA = 36;
    private static final double ANCHO_BOTON_REINICIAR = 82;
    private static final double ANCHO_BOTON_MENU = 62;

    private final HBox raiz;
    private final Label estado;
    private final Label dinero;
    private final Label puntaje;
    private final Label seleccion;
    private final Label mensaje;
    private final Map<TipoTorreta, Button> botones;
    private TipoTorreta tipoSeleccionado;

    public BarraJuego(Consumer<TipoTorreta> alSeleccionarTorreta, Runnable alReiniciarNivel, Runnable alVolverAlMenu) {
        if (alSeleccionarTorreta == null || alReiniciarNivel == null || alVolverAlMenu == null) {
            throw new IllegalArgumentException("La accion de seleccion no puede ser null");
        }

        this.estado = new Label();
        this.dinero = new Label();
        this.puntaje = new Label();
        this.seleccion = new Label();
        this.mensaje = new Label();
        this.botones = new EnumMap<>(TipoTorreta.class);
        this.tipoSeleccionado = TipoTorreta.SIMPLE;
        this.raiz = crearRaiz(alSeleccionarTorreta, alReiniciarNivel, alVolverAlMenu);
        actualizarBotones();
    }

    public HBox obtenerRaiz() {
        return raiz;
    }

    public double obtenerAlto() {
        return ALTO;
    }

    public TipoTorreta obtenerTipoSeleccionado() {
        return tipoSeleccionado;
    }

    public void actualizar(Juego juego) {
        estado.setText(juego.obtenerEstado() + " | N" + (juego.obtenerIndiceNivelActual() + 1));
        dinero.setText("$" + juego.obtenerJugador().obtenerDineroDisponible());
        puntaje.setText("Pts " + juego.obtenerJugador().obtenerPuntajeAcumulado());
        seleccion.setText(tipoSeleccionado.name());
    }

    public void mostrarMensaje(String texto) {
        mensaje.setText(texto);
    }

    private HBox crearRaiz(Consumer<TipoTorreta> alSeleccionarTorreta, Runnable alReiniciarNivel, Runnable alVolverAlMenu) {
        HBox barra = new HBox(10);
        barra.setMinHeight(ALTO);
        barra.setPrefHeight(ALTO);
        barra.setStyle("-fx-padding: 10; -fx-background-color: #20242a; -fx-text-fill: white;");

        estado.setTextFill(Color.WHITE);
        dinero.setTextFill(Color.WHITE);
        puntaje.setTextFill(Color.WHITE);
        seleccion.setTextFill(Color.WHITE);
        mensaje.setTextFill(Color.WHITE);

        Region espacioFlexible = new Region();
        HBox.setHgrow(espacioFlexible, Priority.ALWAYS);

        barra.getChildren().addAll(
                estado,
                dinero,
                puntaje,
                seleccion,
                crearBotonTorreta("S", "Torreta simple", TipoTorreta.SIMPLE, alSeleccionarTorreta),
                crearBotonTorreta("R", "Torreta rapida", TipoTorreta.RAPIDA, alSeleccionarTorreta),
                crearBotonTorreta("P", "Torreta potente", TipoTorreta.POTENTE, alSeleccionarTorreta),
                crearBotonTorreta("D", "Torreta distante", TipoTorreta.DISTANTE, alSeleccionarTorreta),
                mensaje,
                espacioFlexible,
                crearBotonAccion("Reiniciar", "Reiniciar nivel", ANCHO_BOTON_REINICIAR, alReiniciarNivel),
                crearBotonAccion("Menu", "Volver al menu", ANCHO_BOTON_MENU, alVolverAlMenu)
        );

        return barra;
    }

    private Button crearBotonTorreta(String texto, String descripcion, TipoTorreta tipo, Consumer<TipoTorreta> alSeleccionarTorreta) {
        Button boton = new Button(texto);
        boton.setFocusTraversable(false);
        boton.setTooltip(new Tooltip(descripcion));
        boton.setMinWidth(ANCHO_BOTON_TORRETA);
        boton.setPrefWidth(ANCHO_BOTON_TORRETA);
        boton.setOnAction(evento -> {
            tipoSeleccionado = tipo;
            mensaje.setText("");
            actualizarBotones();
            alSeleccionarTorreta.accept(tipo);
        });
        botones.put(tipo, boton);
        return boton;
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
        for (Map.Entry<TipoTorreta, Button> entrada : botones.entrySet()) {
            if (entrada.getKey() == tipoSeleccionado) {
                entrada.getValue().setStyle("-fx-background-color: #f5f0a6; -fx-text-fill: #20242a;");
            } else {
                entrada.getValue().setStyle("");
            }
        }
    }
}
