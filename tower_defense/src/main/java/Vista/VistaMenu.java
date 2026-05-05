package Vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.function.IntConsumer;

public class VistaMenu {
    private static final int CANTIDAD_NIVELES = 3;
    private static final double ANCHO_BOTON_MENU = 180;
    private static final double ALTO_BOTON = 42;
    private static final String ESTILO_TITULO = "-fx-font-size: 42; -fx-font-weight: bold; -fx-text-fill: #f3f0df;";
    private static final String ESTILO_BOTON = "-fx-background-color: #30363f; -fx-text-fill: #f4f0df; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-color: #7c8a74; -fx-border-radius: 6;";
    private static final String ESTILO_BOTON_HOVER = "-fx-background-color: #46505d; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-color: #f5f0a6; -fx-border-radius: 6;";

    private final VBox raiz;

    public VistaMenu(int nivelMaximoDesbloqueado, IntConsumer alSeleccionarNivel, Runnable alSalir) {
        if (alSeleccionarNivel == null || alSalir == null) {
            throw new IllegalArgumentException("Las acciones del menu no pueden ser null");
        }

        if (nivelMaximoDesbloqueado < 1 || nivelMaximoDesbloqueado > CANTIDAD_NIVELES) {
            throw new IllegalArgumentException("El nivel maximo desbloqueado no es valido");
        }

        this.raiz = crearRaiz(nivelMaximoDesbloqueado, alSeleccionarNivel, alSalir);
    }

    public VBox obtenerRaiz() {
        return raiz;
    }

    private VBox crearRaiz(int nivelMaximoDesbloqueado, IntConsumer alSeleccionarNivel, Runnable alSalir) {
        Label titulo = new Label("Tower Defense");
        titulo.setStyle(ESTILO_TITULO);

        VBox selectorNiveles = new VBox(10);
        selectorNiveles.setAlignment(Pos.CENTER);

        for (int nivel = 1; nivel <= CANTIDAD_NIVELES; nivel++) {
            int nivelSeleccionado = nivel;
            boolean desbloqueado = nivel <= nivelMaximoDesbloqueado;
            Button botonNivel = crearBoton("Nivel " + nivel, desbloqueado);
            botonNivel.setPrefWidth(ANCHO_BOTON_MENU);
            botonNivel.setOnAction(evento -> alSeleccionarNivel.accept(nivelSeleccionado));
            selectorNiveles.getChildren().add(botonNivel);
        }

        Button salir = crearBoton("Salir", true);
        salir.setPrefWidth(ANCHO_BOTON_MENU);
        salir.setOnAction(evento -> alSalir.run());

        VBox contenedor = new VBox(20, titulo, selectorNiveles, salir);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(32));
        contenedor.setBackground(crearFondo());
        return contenedor;
    }

    private Button crearBoton(String texto, boolean habilitado) {
        Button boton = new Button(texto);
        boton.setFocusTraversable(false);
        boton.setMinHeight(ALTO_BOTON);
        boton.setPrefHeight(ALTO_BOTON);
        boton.setDisable(!habilitado);
        boton.setStyle(ESTILO_BOTON);

        if (habilitado) {
            boton.setOnMouseEntered(evento -> boton.setStyle(ESTILO_BOTON_HOVER));
            boton.setOnMouseExited(evento -> boton.setStyle(ESTILO_BOTON));
        }

        return boton;
    }

    private Background crearFondo() {
        InputStream stream = getClass().getResourceAsStream("/assets/escenario/piso/green_bones_4.png");
        if (stream == null) {
            return Background.EMPTY;
        }

        Image imagen = new Image(stream);
        BackgroundSize tamanio = new BackgroundSize(64, 64, false, false, false, false);
        BackgroundImage fondo = new BackgroundImage(
                imagen,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                BackgroundPosition.DEFAULT,
                tamanio
        );
        return new Background(fondo);
    }
}
