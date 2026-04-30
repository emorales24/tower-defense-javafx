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
    private static final double ANCHO_VENTANA = 800;
    private static final double ALTO_VENTANA = 600;

    @Override
    public void start(Stage stage) {
        try {
            Juego juego = new CargadorJuegoXml().cargar();
            juego.iniciar();
            mostrarJuegoCargado(stage, juego);
        } catch (CargaNivelException | IllegalArgumentException e) {
            mostrarError(stage, e.getMessage());
        }
    }

    private void mostrarJuegoCargado(Stage stage, Juego juego) {
        VBox contenedor = crearContenedor();
        contenedor.getChildren().addAll(
                new Label("Tower Defense"),
                new Label("Estado: " + juego.obtenerEstado()),
                new Label("Nivel actual: " + (juego.obtenerIndiceNivelActual() + 1)),
                new Label("Niveles cargados: " + juego.obtenerNiveles().size()),
                new Label("Dinero inicial: " + juego.obtenerJugador().obtenerDineroDisponible())
        );

        configurarStage(stage, new Scene(contenedor, ANCHO_VENTANA, ALTO_VENTANA));
    }

    private void mostrarError(Stage stage, String mensaje) {
        VBox contenedor = crearContenedor();
        contenedor.getChildren().addAll(
                new Label("No se pudo cargar el juego"),
                new Label(mensaje)
        );

        configurarStage(stage, new Scene(contenedor, ANCHO_VENTANA, ALTO_VENTANA));
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
