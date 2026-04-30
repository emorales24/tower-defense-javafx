package Vista;

import Carga.CargaNivelException;
import Carga.CargadorJuegoXml;
import Modelo.Juego;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        try {
            Juego juego = new CargadorJuegoXml().cargar();
            mostrarJuegoCargado(stage, juego);
        } catch (CargaNivelException | IllegalArgumentException e) {
            mostrarError(stage, e.getMessage());
        }
    }

    private void mostrarJuegoCargado(Stage stage, Juego juego) {
        VistaJuego vistaJuego = new VistaJuego(juego);
        configurarStage(stage, new Scene(vistaJuego.obtenerRaiz(), vistaJuego.obtenerAncho(), vistaJuego.obtenerAlto()));
        vistaJuego.iniciar();
    }

    private void mostrarError(Stage stage, String mensaje) {
        VBox contenedor = crearContenedor();
        contenedor.getChildren().addAll(
                new Label("No se pudo cargar el juego"),
                new Label(mensaje)
        );

        configurarStage(stage, new Scene(contenedor, 800, 600));
    }

    private VBox crearContenedor() {
        VBox contenedor = new VBox(12);
        contenedor.setPadding(new Insets(24));
        return contenedor;
    }

    private void configurarStage(Stage stage, Scene scene) {
        stage.setTitle("Tower Defense");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
