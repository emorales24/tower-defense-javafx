package Carga;

import Modelo.Jugador;
import Modelo.enemigo.TipoEnemigo;
import Modelo.escena.Base;
import Modelo.escena.Escenario;
import Modelo.escena.Ruta;
import Modelo.escena.Slot;
import Modelo.geometria.Dimension;
import Modelo.geometria.Posicion;
import Modelo.nivel.Nivel;
import Modelo.nivel.SpawnEnemigos;
import Modelo.torreta.TipoTorreta;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class CargadorNivelXml implements CargadorNivel {
    private static final String RAIZ_NIVEL = "nivel";
    private static final String RUTA_XSD = "niveles/nivel.xsd";

    @Override
    public Nivel cargar(String rutaArchivo, Jugador jugador) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacia");
        }

        if (jugador == null) {
            throw new IllegalArgumentException("El jugador no puede ser null");
        }

        try {
            byte[] contenido = leerContenido(rutaArchivo);
            validarXml(contenido, rutaArchivo);
            Document documento = crearDocumentBuilder().parse(new ByteArrayInputStream(contenido));
            documento.getDocumentElement().normalize();
            return construirNivel(rutaArchivo, jugador, documento);
        } catch (SAXParseException e) {
            throw new CargaNivelException(
                    "XML invalido en " + rutaArchivo
                            + ", linea " + e.getLineNumber()
                            + ", columna " + e.getColumnNumber()
                            + ": " + e.getMessage(),
                    e
            );
        } catch (SAXException e) {
            throw new CargaNivelException("XML invalido en " + rutaArchivo + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CargaNivelException("No se pudo leer el archivo de nivel: " + rutaArchivo, e);
        } catch (ParserConfigurationException e) {
            throw new CargaNivelException("No se pudo configurar el parser XML", e);
        } catch (IllegalArgumentException e) {
            throw new CargaNivelException("Error al construir el nivel desde " + rutaArchivo + ": " + e.getMessage(), e);
        }
    }

    private Nivel construirNivel(String rutaArchivo, Jugador jugador, Document documento) {
        Element nivel = documento.getDocumentElement();
        if (!RAIZ_NIVEL.equals(nivel.getTagName())) {
            throw error(rutaArchivo, nivel, "el elemento raiz debe ser <nivel>");
        }

        double ancho = numeroObligatorio(rutaArchivo, nivel, "ancho");
        double alto = numeroObligatorio(rutaArchivo, nivel, "alto");
        double dineroInicial = numeroObligatorio(rutaArchivo, nivel, "dineroInicial");

        Element spawnElemento = unicoHijoObligatorio(rutaArchivo, nivel, "spawn");
        Posicion spawn = posicionDesdeAtributos(rutaArchivo, spawnElemento);

        Element baseElemento = unicoHijoObligatorio(rutaArchivo, nivel, "baseJugador");
        Base base = new Base(
                posicionDesdeAtributos(rutaArchivo, baseElemento),
                numeroObligatorio(rutaArchivo, baseElemento, "vida")
        );

        Ruta ruta = leerRuta(rutaArchivo, nivel);
        List<Slot> slots = leerSlots(rutaArchivo, nivel);
        List<SpawnEnemigos> enemigos = leerEnemigos(rutaArchivo, nivel);
        Map<Slot, TipoTorreta> torretasIniciales = leerTorretasIniciales(rutaArchivo, nivel, slots);

        Escenario escenario = new Escenario(ancho, alto, ruta, spawn, base, slots);
        return new Nivel(escenario, jugador, dineroInicial, enemigos, torretasIniciales);
    }

    private Ruta leerRuta(String rutaArchivo, Element nivel) {
        Element rutaElemento = unicoHijoObligatorio(rutaArchivo, nivel, "ruta");
        TreeMap<Integer, Posicion> puntosPorOrden = new TreeMap<>();

        for (Element punto : hijos(rutaElemento, "punto")) {
            int orden = enteroPositivoObligatorio(rutaArchivo, punto, "orden");
            Posicion posicion = posicionDesdeAtributos(rutaArchivo, punto);

            if (puntosPorOrden.put(orden, posicion) != null) {
                throw error(rutaArchivo, punto, "orden duplicado en la ruta: " + orden);
            }
        }

        if (puntosPorOrden.size() < 2) {
            throw error(rutaArchivo, rutaElemento, "la ruta debe tener al menos dos puntos");
        }

        List<Posicion> puntos = new ArrayList<>(puntosPorOrden.values());
        if (rutaElemento.hasAttribute("ancho")) {
            return new Ruta(puntos, numeroObligatorio(rutaArchivo, rutaElemento, "ancho"));
        }

        return new Ruta(puntos);
    }

    private List<Slot> leerSlots(String rutaArchivo, Element nivel) {
        Element slotsElemento = unicoHijoObligatorio(rutaArchivo, nivel, "slots");
        Map<String, Slot> slotsPorPosicion = new LinkedHashMap<>();

        for (Element slotElemento : hijos(slotsElemento, "slot")) {
            Posicion posicion = posicionDesdeAtributos(rutaArchivo, slotElemento);
            Slot slot = crearSlot(rutaArchivo, slotElemento, posicion);
            String clave = clavePosicion(posicion);

            if (slotsPorPosicion.put(clave, slot) != null) {
                throw error(rutaArchivo, slotElemento, "slot duplicado en posicion " + clave);
            }
        }

        return new ArrayList<>(slotsPorPosicion.values());
    }

    private Slot crearSlot(String rutaArchivo, Element slotElemento, Posicion posicion) {
        boolean tieneAncho = slotElemento.hasAttribute("ancho");
        boolean tieneAlto = slotElemento.hasAttribute("alto");

        if (!tieneAncho && !tieneAlto) {
            return new Slot(posicion);
        }

        if (tieneAncho != tieneAlto) {
            throw error(rutaArchivo, slotElemento, "si se define ancho del slot tambien debe definirse alto, y viceversa");
        }

        return new Slot(
                posicion,
                new Dimension(
                        numeroObligatorio(rutaArchivo, slotElemento, "ancho"),
                        numeroObligatorio(rutaArchivo, slotElemento, "alto")
                )
        );
    }

    private List<SpawnEnemigos> leerEnemigos(String rutaArchivo, Element nivel) {
        Element enemigosElemento = unicoHijoObligatorio(rutaArchivo, nivel, "enemigos");
        List<SpawnEnemigos> enemigos = new ArrayList<>();

        for (Element enemigo : hijos(enemigosElemento, "enemigo")) {
            enemigos.add(new SpawnEnemigos(
                    enumObligatorio(TipoEnemigo.class, rutaArchivo, enemigo, "tipo"),
                    numeroObligatorio(rutaArchivo, enemigo, "tiempo")
            ));
        }

        return enemigos;
    }

    private Map<Slot, TipoTorreta> leerTorretasIniciales(String rutaArchivo, Element nivel, List<Slot> slots) {
        Map<Slot, TipoTorreta> torretas = new LinkedHashMap<>();
        List<Element> secciones = hijos(nivel, "torretasIniciales");
        if (secciones.isEmpty()) {
            return torretas;
        }

        if (secciones.size() > 1) {
            throw error(rutaArchivo, nivel, "solo puede existir una seccion <torretasIniciales>");
        }

        Map<String, Slot> slotsPorPosicion = new HashMap<>();
        for (Slot slot : slots) {
            slotsPorPosicion.put(clavePosicion(slot.obtenerPosicion()), slot);
        }

        for (Element torreta : hijos(secciones.get(0), "torreta")) {
            Posicion posicion = new Posicion(
                    numeroObligatorio(rutaArchivo, torreta, "slotX"),
                    numeroObligatorio(rutaArchivo, torreta, "slotY")
            );

            Slot slot = slotsPorPosicion.get(clavePosicion(posicion));
            if (slot == null) {
                throw error(rutaArchivo, torreta, "la torreta inicial referencia un slot inexistente en " + clavePosicion(posicion));
            }

            TipoTorreta tipo = enumObligatorio(TipoTorreta.class, rutaArchivo, torreta, "tipo");
            if (torretas.put(slot, tipo) != null) {
                throw error(rutaArchivo, torreta, "hay mas de una torreta inicial para el mismo slot");
            }
        }

        return torretas;
    }

    private DocumentBuilder crearDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setExpandEntityReferences(false);
        factory.setNamespaceAware(false);
        return factory.newDocumentBuilder();
    }

    private byte[] leerContenido(String rutaArchivo) throws IOException {
        try (InputStream archivo = abrirArchivo(rutaArchivo)) {
            return archivo.readAllBytes();
        }
    }

    private void validarXml(byte[] contenidoXml, String rutaArchivo) throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        try (InputStream xsd = abrirRecursoXsd()) {
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(contenidoXml)));
        }
    }

    private InputStream abrirRecursoXsd() {
        InputStream recurso = Thread.currentThread().getContextClassLoader().getResourceAsStream(RUTA_XSD);
        if (recurso == null) {
            throw new CargaNivelException("No se encontro el archivo XSD: " + RUTA_XSD);
        }

        return recurso;
    }

    private InputStream abrirArchivo(String rutaArchivo) throws IOException {
        Path ruta = Paths.get(rutaArchivo);
        if (Files.exists(ruta)) {
            return Files.newInputStream(ruta);
        }

        InputStream recurso = Thread.currentThread().getContextClassLoader().getResourceAsStream(rutaArchivo);
        if (recurso != null) {
            return recurso;
        }

        throw new CargaNivelException("No existe el archivo de nivel: " + rutaArchivo);
    }

    private Element unicoHijoObligatorio(String rutaArchivo, Element padre, String nombre) {
        List<Element> elementos = hijos(padre, nombre);
        if (elementos.isEmpty()) {
            throw error(rutaArchivo, padre, "falta el elemento <" + nombre + ">");
        }

        if (elementos.size() > 1) {
            throw error(rutaArchivo, padre, "solo puede existir un elemento <" + nombre + ">");
        }

        return elementos.get(0);
    }

    private List<Element> hijos(Element padre, String nombre) {
        List<Element> elementos = new ArrayList<>();
        for (Node nodo = padre.getFirstChild(); nodo != null; nodo = nodo.getNextSibling()) {
            if (nodo.getNodeType() == Node.ELEMENT_NODE && nombre.equals(nodo.getNodeName())) {
                elementos.add((Element) nodo);
            }
        }

        return elementos;
    }

    private Posicion posicionDesdeAtributos(String rutaArchivo, Element elemento) {
        return new Posicion(
                numeroObligatorio(rutaArchivo, elemento, "x"),
                numeroObligatorio(rutaArchivo, elemento, "y")
        );
    }

    private double numeroObligatorio(String rutaArchivo, Element elemento, String atributo) {
        String valor = atributoObligatorio(rutaArchivo, elemento, atributo);
        try {
            double numero = Double.parseDouble(valor);
            if (Double.isNaN(numero) || Double.isInfinite(numero)) {
                throw new NumberFormatException();
            }

            return numero;
        } catch (NumberFormatException e) {
            throw error(rutaArchivo, elemento, "el atributo " + atributo + " debe ser numerico y finito");
        }
    }

    private int enteroPositivoObligatorio(String rutaArchivo, Element elemento, String atributo) {
        double valor = numeroObligatorio(rutaArchivo, elemento, atributo);
        if (valor <= 0 || valor != Math.floor(valor)) {
            throw error(rutaArchivo, elemento, "el atributo " + atributo + " debe ser un entero positivo");
        }

        return (int) valor;
    }

    private <E extends Enum<E>> E enumObligatorio(Class<E> enumClass, String rutaArchivo, Element elemento, String atributo) {
        String valor = atributoObligatorio(rutaArchivo, elemento, atributo).toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(enumClass, valor);
        } catch (IllegalArgumentException e) {
            throw error(rutaArchivo, elemento, "valor invalido en atributo " + atributo + ": " + valor);
        }
    }

    private String atributoObligatorio(String rutaArchivo, Element elemento, String atributo) {
        if (!elemento.hasAttribute(atributo) || elemento.getAttribute(atributo).trim().isEmpty()) {
            throw error(rutaArchivo, elemento, "falta el atributo " + atributo);
        }

        return elemento.getAttribute(atributo).trim();
    }

    private String clavePosicion(Posicion posicion) {
        return posicion.obtenerX() + "," + posicion.obtenerY();
    }

    private CargaNivelException error(String rutaArchivo, Element elemento, String mensaje) {
        return new CargaNivelException(
                "Error en " + rutaArchivo
                        + ", elemento <" + elemento.getTagName() + ">"
                        + ": " + mensaje
        );
    }
}
