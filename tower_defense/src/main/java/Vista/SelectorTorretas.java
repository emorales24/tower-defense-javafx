package Vista;

import Modelo.torreta.TipoTorreta;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class SelectorTorretas {
    private static final double ALTO = 72;
    private static final double ANCHO_BOTON = 118;
    private static final double ALTO_BOTON = 56;
    private static final double TAMANIO_ICONO = 30;
    private static final String ESTILO_BASE = "-fx-background-color: #20242a; -fx-padding: 8 10 8 10;";
    private static final String ESTILO_BOTON = "-fx-background-color: #30363f; -fx-text-fill: #f4f0df; -fx-background-radius: 6; -fx-border-color: #59616b; -fx-border-radius: 6;";
    private static final String ESTILO_BOTON_SELECCIONADO = "-fx-background-color: #f5f0a6; -fx-text-fill: #20242a; -fx-background-radius: 6; -fx-border-color: #ffffff; -fx-border-radius: 6;";
    private static final String ESTILO_BOTON_BLOQUEADO = "-fx-background-color: #15191d; -fx-text-fill: #858a8f; -fx-background-radius: 6; -fx-border-color: #33383d; -fx-border-radius: 6;";
    private static final String ESTILO_TEXTO = "-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #fff7d6; -fx-effect: dropshadow(one-pass-box, #101010, 2, 1, 0, 1);";

    private final HBox raiz;
    private final AssetsJuego assets;
    private final Map<TipoTorreta, Button> botones;
    private final Map<TipoTorreta, Double> costos;
    private TipoTorreta tipoSeleccionado;
    private double dineroDisponible;

    public SelectorTorretas(Consumer<TipoTorreta> alSeleccionarTorreta) {
        if (alSeleccionarTorreta == null) {
            throw new IllegalArgumentException("La accion de seleccion no puede ser null");
        }

        this.assets = new AssetsJuego();
        this.botones = new EnumMap<>(TipoTorreta.class);
        this.costos = crearCostos();
        this.tipoSeleccionado = TipoTorreta.SIMPLE;
        this.dineroDisponible = Double.MAX_VALUE;
        this.raiz = crearRaiz(alSeleccionarTorreta);
        actualizarBotones();
    }

    public HBox obtenerRaiz() {
        return raiz;
    }

    public double obtenerAlto() {
        return ALTO;
    }

    public void seleccionar(TipoTorreta tipo) {
        tipoSeleccionado = tipo;
        actualizarBotones();
    }

    public void actualizarDisponibilidad(double dineroDisponible) {
        this.dineroDisponible = dineroDisponible;
        actualizarBotones();
    }

    private HBox crearRaiz(Consumer<TipoTorreta> alSeleccionarTorreta) {
        HBox barra = new HBox(10);
        barra.setMinHeight(ALTO);
        barra.setPrefHeight(ALTO);
        barra.setAlignment(Pos.CENTER);
        barra.setStyle(ESTILO_BASE);

        barra.getChildren().addAll(
                crearBoton(TipoTorreta.SIMPLE, "Simple", "Torreta simple"),
                crearBoton(TipoTorreta.RAPIDA, "Rapida", "Torreta rapida"),
                crearBoton(TipoTorreta.POTENTE, "Potente", "Torreta potente"),
                crearBoton(TipoTorreta.DISTANTE, "Distante", "Torreta distante")
        );

        botones.forEach((tipo, boton) -> {
            boton.setOnMouseEntered(evento -> seleccionarYNotificar(tipo, alSeleccionarTorreta));
            boton.setOnAction(evento -> seleccionarYNotificar(tipo, alSeleccionarTorreta));
        });

        return barra;
    }

    private Button crearBoton(TipoTorreta tipo, String nombre, String descripcion) {
        ImageView icono = new ImageView(assets.obtenerTorreta(tipo));
        icono.setFitWidth(TAMANIO_ICONO);
        icono.setFitHeight(TAMANIO_ICONO);
        icono.setPreserveRatio(true);

        String costo = "$" + Math.round(costos.get(tipo));
        Label texto = new Label(nombre + " " + costo);
        texto.setStyle(ESTILO_TEXTO);

        VBox contenido = new VBox(2, icono, texto);
        contenido.setAlignment(Pos.CENTER);

        Button boton = new Button();
        boton.setGraphic(contenido);
        boton.setFocusTraversable(false);
        boton.setTooltip(new Tooltip(descripcion + " - " + costo));
        boton.setMinWidth(ANCHO_BOTON);
        boton.setPrefWidth(ANCHO_BOTON);
        boton.setMinHeight(ALTO_BOTON);
        boton.setPrefHeight(ALTO_BOTON);
        botones.put(tipo, boton);
        return boton;
    }

    private void seleccionarYNotificar(TipoTorreta tipo, Consumer<TipoTorreta> alSeleccionarTorreta) {
        if (!puedeComprar(tipo)) {
            return;
        }

        seleccionar(tipo);
        alSeleccionarTorreta.accept(tipo);
    }

    private void actualizarBotones() {
        for (Map.Entry<TipoTorreta, Button> entrada : botones.entrySet()) {
            TipoTorreta tipo = entrada.getKey();
            Button boton = entrada.getValue();

            if (!puedeComprar(tipo)) {
                boton.setStyle(ESTILO_BOTON_BLOQUEADO);
                boton.setOpacity(0.5);
                continue;
            }

            boton.setOpacity(1);
            boton.setStyle(tipo == tipoSeleccionado ? ESTILO_BOTON_SELECCIONADO : ESTILO_BOTON);
        }
    }

    private boolean puedeComprar(TipoTorreta tipo) {
        return dineroDisponible >= costos.get(tipo);
    }

    private Map<TipoTorreta, Double> crearCostos() {
        Map<TipoTorreta, Double> costosTorretas = new EnumMap<>(TipoTorreta.class);
        costosTorretas.put(TipoTorreta.SIMPLE, 50.0);
        costosTorretas.put(TipoTorreta.RAPIDA, 75.0);
        costosTorretas.put(TipoTorreta.POTENTE, 100.0);
        costosTorretas.put(TipoTorreta.DISTANTE, 100.0);
        return costosTorretas;
    }
}
