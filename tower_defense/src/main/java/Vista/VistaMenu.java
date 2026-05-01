package Vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.IntConsumer;

public class VistaMenu {
    private static final int CANTIDAD_NIVELES = 3;

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
        titulo.setStyle("-fx-font-size: 32; -fx-font-weight: bold;");

        Label subtitulo = new Label("Seleccionar nivel");

        HBox niveles = new HBox(12);
        niveles.setAlignment(Pos.CENTER);
        for (int nivel = 1; nivel <= CANTIDAD_NIVELES; nivel++) {
            niveles.getChildren().add(crearBotonNivel(nivel, nivelMaximoDesbloqueado, alSeleccionarNivel));
        }

        Button salir = new Button("Salir");
        salir.setPrefWidth(180);
        salir.setOnAction(evento -> alSalir.run());

        VBox contenedor = new VBox(18, titulo, subtitulo, niveles, salir);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(32));
        contenedor.setStyle("-fx-background-color: #eef2e6;");
        return contenedor;
    }

    private Button crearBotonNivel(int nivel, int nivelMaximoDesbloqueado, IntConsumer alSeleccionarNivel) {
        Button boton = new Button(nivel <= nivelMaximoDesbloqueado ? "Nivel " + nivel : "Nivel " + nivel + " bloqueado");
        boton.setPrefWidth(140);
        boton.setDisable(nivel > nivelMaximoDesbloqueado);
        boton.setOnAction(evento -> alSeleccionarNivel.accept(nivel));
        return boton;
    }
}
