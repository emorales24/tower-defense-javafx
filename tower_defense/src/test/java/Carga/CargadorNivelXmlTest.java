package Carga;

import Modelo.Jugador;
import Modelo.geometria.Posicion;
import Modelo.nivel.EstadoNivel;
import Modelo.nivel.Nivel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CargadorNivelXmlTest {

    @TempDir
    Path carpetaTemporal;

    @Test
    void cargaNivelValidoDesdeResources() {
        CargadorNivel cargador = new CargadorNivelXml();
        Jugador jugador = new Jugador();

        Nivel nivel = cargador.cargar("niveles/nivel1.xml", jugador);

        assertEquals(800, nivel.obtenerEscenario().obtenerAncho(), 0.0001);
        assertEquals(600, nivel.obtenerEscenario().obtenerAlto(), 0.0001);
        assertPosicion(0, 300, nivel.obtenerEscenario().obtenerPuntoSpawn());
        assertPosicion(760, 120, nivel.obtenerEscenario().obtenerBaseJugador().obtenerPosicion());
        assertEquals(4, nivel.obtenerEscenario().obtenerRuta().cantidadPuntos());
        assertEquals(4, nivel.obtenerEscenario().obtenerSlots().size());

        nivel.iniciar();

        assertEquals(EstadoNivel.EN_CURSO, nivel.obtenerEstado());
        assertEquals(200, jugador.obtenerDineroDisponible(), 0.0001);
        assertEquals(4, nivel.obtenerSpawnsPendientes().size());
        assertEquals(0, nivel.obtenerTorretasInstaladas().size());
    }

    @Test
    void fallaSiElXmlNoRespetaElXsd() throws IOException {
        Path archivo = escribirXml(
                "nivel_invalido.xml",
                xmlSinEnemigos()
        );

        CargadorNivel cargador = new CargadorNivelXml();

        assertThrows(
                CargaNivelException.class,
                () -> cargador.cargar(archivo.toString(), new Jugador())
        );
    }

    @Test
    void fallaSiLaTorretaInicialReferenciaUnSlotInexistente() throws IOException {
        Path archivo = escribirXml(
                "torreta_en_slot_inexistente.xml",
                xmlBaseConTorretas(
                        "    <torreta slotX=\"90\" slotY=\"90\" tipo=\"SIMPLE\"/>"
                )
        );

        CargadorNivel cargador = new CargadorNivelXml();

        assertThrows(
                CargaNivelException.class,
                () -> cargador.cargar(archivo.toString(), new Jugador())
        );
    }

    @Test
    void fallaSiUnSlotColisionaConLaRuta() throws IOException {
        Path archivo = escribirXml(
                "slot_sobre_ruta.xml",
                xmlConSlots(
                        "        <slot x=\"50\" y=\"0\" ancho=\"32\" alto=\"32\"/>"
                )
        );

        CargadorNivel cargador = new CargadorNivelXml();

        assertThrows(
                CargaNivelException.class,
                () -> cargador.cargar(archivo.toString(), new Jugador())
        );
    }

    private Path escribirXml(String nombre, String contenido) throws IOException {
        Path archivo = carpetaTemporal.resolve(nombre);
        Files.writeString(archivo, contenido);
        return archivo;
    }

    private void assertPosicion(double x, double y, Posicion posicion) {
        assertEquals(x, posicion.obtenerX(), 0.0001);
        assertEquals(y, posicion.obtenerY(), 0.0001);
    }

    private String xmlBaseConTorretas(String torretas) {
        return String.join(
                System.lineSeparator(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<nivel ancho=\"120\" alto=\"100\" dineroInicial=\"100\">",
                "    <spawn x=\"0\" y=\"0\"/>",
                "    <baseJugador x=\"100\" y=\"0\" vida=\"10\"/>",
                "    <ruta ancho=\"20\">",
                "        <punto orden=\"1\" x=\"0\" y=\"0\"/>",
                "        <punto orden=\"2\" x=\"100\" y=\"0\"/>",
                "    </ruta>",
                "    <slots>",
                "        <slot x=\"50\" y=\"50\" ancho=\"32\" alto=\"32\"/>",
                "    </slots>",
                "    <enemigos>",
                "        <enemigo tipo=\"DEBIL\" tiempo=\"0\"/>",
                "    </enemigos>",
                "    <torretasIniciales>",
                torretas,
                "    </torretasIniciales>",
                "</nivel>",
                ""
        );
    }

    private String xmlSinEnemigos() {
        return String.join(
                System.lineSeparator(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<nivel ancho=\"120\" alto=\"100\" dineroInicial=\"100\">",
                "    <spawn x=\"0\" y=\"0\"/>",
                "    <baseJugador x=\"100\" y=\"0\" vida=\"10\"/>",
                "    <ruta ancho=\"20\">",
                "        <punto orden=\"1\" x=\"0\" y=\"0\"/>",
                "        <punto orden=\"2\" x=\"100\" y=\"0\"/>",
                "    </ruta>",
                "    <slots>",
                "        <slot x=\"50\" y=\"50\" ancho=\"32\" alto=\"32\"/>",
                "    </slots>",
                "</nivel>",
                ""
        );
    }

    private String xmlConSlots(String slots) {
        return String.join(
                System.lineSeparator(),
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<nivel ancho=\"120\" alto=\"100\" dineroInicial=\"100\">",
                "    <spawn x=\"0\" y=\"0\"/>",
                "    <baseJugador x=\"100\" y=\"0\" vida=\"10\"/>",
                "    <ruta ancho=\"20\">",
                "        <punto orden=\"1\" x=\"0\" y=\"0\"/>",
                "        <punto orden=\"2\" x=\"100\" y=\"0\"/>",
                "    </ruta>",
                "    <slots>",
                slots,
                "    </slots>",
                "    <enemigos>",
                "        <enemigo tipo=\"DEBIL\" tiempo=\"0\"/>",
                "    </enemigos>",
                "</nivel>",
                ""
        );
    }
}
