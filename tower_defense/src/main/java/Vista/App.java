package Vista;

import Carga.CargaNivelException;
import Carga.CargadorJuegoXml;
import Modelo.Juego;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {
    private static final double ANCHO_MENU = 800;
    private static final double ALTO_MENU = 600;
    private static final int CANTIDAD_NIVELES = 3;

    private Stage stage;
    private int nivelMaximoDesbloqueado;
    private int nivelInicialActual;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.nivelMaximoDesbloqueado = 1;
        this.nivelInicialActual = 1;
        stage.setTitle("Tower Defense");
        stage.setResizable(false);
        mostrarMenu();
    }

    private void mostrarMenu() {
        VistaMenu menu = new VistaMenu(nivelMaximoDesbloqueado, this::iniciarJuegoDesdeNivel, Platform::exit);
        stage.setScene(new Scene(menu.obtenerRaiz(), ANCHO_MENU, ALTO_MENU));
        stage.show();
    }

    private void iniciarJuegoDesdeNivel(int numeroNivel) {
        try {
            nivelInicialActual = numeroNivel;
            Juego juego = new CargadorJuegoXml().cargarDesdeNivel(numeroNivel);
            mostrarJuego(juego);
        } catch (CargaNivelException | IllegalArgumentException e) {
            mostrarAlerta("No se pudo cargar el juego", e.getMessage());
            mostrarMenu();
        }
    }

    private void mostrarJuego(Juego juego) {
        VistaJuego vistaJuego = new VistaJuego(
                juego,
                nivelSiguiente -> {
                    int nivelRealSiguiente = nivelInicialActual + nivelSiguiente - 1;
                    desbloquearHasta(nivelRealSiguiente);
                    mostrarAlerta("Victoria", "Nivel completado. Sigue el nivel " + nivelRealSiguiente);
                },
                () -> {
                    mostrarAlerta("Derrota", "La base fue destruida");
                    mostrarMenu();
                },
                () -> {
                    desbloquearHasta(CANTIDAD_NIVELES);
                    mostrarAlerta("Victoria final", "Felicitaciones, ganaste el juego");
                    mostrarMenu();
                },
                this::mostrarMenu
        );

        stage.setScene(new Scene(vistaJuego.obtenerRaiz(), vistaJuego.obtenerAncho(), vistaJuego.obtenerAlto()));
        stage.show();
        vistaJuego.iniciar();
    }

    private void desbloquearHasta(int nivel) {
        nivelMaximoDesbloqueado = Math.max(nivelMaximoDesbloqueado, Math.min(nivel, CANTIDAD_NIVELES));
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.initOwner(stage);
        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
