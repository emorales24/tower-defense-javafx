package Vista;

import Modelo.enemigo.TipoEnemigo;
import Modelo.torreta.TipoTorreta;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class AssetsJuego {
    private final Image baseJugador;
    private final Image monedas;
    private final Image puntaje;
    private final Image slot;
    private final Image ruta;
    private final Image[] pisos;
    private final Map<TipoEnemigo, Image> enemigos;
    private final Map<TipoEnemigo, Image[]> explosiones;
    private final Map<TipoTorreta, Image> torretas;
    private final Map<TipoTorreta, Image[]> proyectiles;

    public AssetsJuego() {
        this.baseJugador = cargar("/assets/base.png");
        this.monedas = cargar("/assets/escenario/monedas.png");
        this.puntaje = cargar("/assets/escenario/puntaje.png");
        this.slot = cargar("/assets/escenario/slots.png");
        this.ruta = cargar("/assets/escenario/ruta/pedestal_full.png");
        this.pisos = cargarPisos();
        this.enemigos = cargarEnemigos();
        this.explosiones = cargarExplosiones();
        this.torretas = cargarTorretas();
        this.proyectiles = cargarProyectiles();
    }

    public Image obtenerBaseJugador() {
        return baseJugador;
    }

    public Image obtenerMonedas() {
        return monedas;
    }

    public Image obtenerPuntaje() {
        return puntaje;
    }

    public Image obtenerSlot() {
        return slot;
    }

    public Image obtenerRuta() {
        return ruta;
    }

    public Image obtenerPiso(int x, int y) {
        int indice = Math.floorMod(x * 31 + y * 17, pisos.length);
        return pisos[indice];
    }

    public Image obtenerEnemigo(TipoEnemigo tipo) {
        return enemigos.get(tipo);
    }

    public Image obtenerTorreta(TipoTorreta tipo) {
        return torretas.get(tipo);
    }

    public Image obtenerProyectil(TipoTorreta tipo, int direccion) {
        Image[] imagenes = proyectiles.get(tipo);
        return imagenes[Math.floorMod(direccion, imagenes.length)];
    }

    public Image[] obtenerExplosion(TipoEnemigo tipo) {
        return explosiones.get(tipo);
    }

    private Map<TipoEnemigo, Image> cargarEnemigos() {
        Map<TipoEnemigo, Image> imagenes = new EnumMap<>(TipoEnemigo.class);
        imagenes.put(TipoEnemigo.DEBIL, cargar("/assets/enemigos/enemigo_debil.png"));
        imagenes.put(TipoEnemigo.MEDIO, cargar("/assets/enemigos/enemigo_medio.png"));
        imagenes.put(TipoEnemigo.FUERTE, cargar("/assets/enemigos/enemigo_fuerte.png"));
        return imagenes;
    }

    private Image[] cargarPisos() {
        return new Image[]{
                cargar("/assets/escenario/piso/green_bones_1.png"),
                cargar("/assets/escenario/piso/green_bones_2.png"),
                cargar("/assets/escenario/piso/green_bones_3.png"),
                cargar("/assets/escenario/piso/green_bones_4.png"),
                cargar("/assets/escenario/piso/green_bones_5.png"),
                cargar("/assets/escenario/piso/green_bones_6.png"),
                cargar("/assets/escenario/piso/green_bones_7.png"),
                cargar("/assets/escenario/piso/green_bones_8.png"),
                cargar("/assets/escenario/piso/green_bones_9.png"),
                cargar("/assets/escenario/piso/green_bones_10.png"),
                cargar("/assets/escenario/piso/green_bones_11.png"),
                cargar("/assets/escenario/piso/green_bones_12.png")
        };
    }

    private Map<TipoEnemigo, Image[]> cargarExplosiones() {
        Map<TipoEnemigo, Image[]> imagenes = new EnumMap<>(TipoEnemigo.class);
        imagenes.put(TipoEnemigo.DEBIL, new Image[]{
                cargar("/assets/enemigos/explosion_enemigo_debil/cloud_mutagenic_large_1.png"),
                cargar("/assets/enemigos/explosion_enemigo_debil/cloud_mutagenic_large_2.png"),
                cargar("/assets/enemigos/explosion_enemigo_debil/cloud_mutagenic_large_3.png"),
                cargar("/assets/enemigos/explosion_enemigo_debil/cloud_mutagenic_large_4.png")
        });
        imagenes.put(TipoEnemigo.MEDIO, new Image[]{
                cargar("/assets/enemigos/explosion_enemigo_medio/cloud_acid_0.png"),
                cargar("/assets/enemigos/explosion_enemigo_medio/cloud_acid_1.png"),
                cargar("/assets/enemigos/explosion_enemigo_medio/cloud_acid_2.png")
        });
        imagenes.put(TipoEnemigo.FUERTE, new Image[]{
                cargar("/assets/enemigos/explosion_enemigo_fuerte/cloud_cold_0.png"),
                cargar("/assets/enemigos/explosion_enemigo_fuerte/cloud_cold_1.png"),
                cargar("/assets/enemigos/explosion_enemigo_fuerte/cloud_cold_2.png")
        });
        return imagenes;
    }

    private Map<TipoTorreta, Image> cargarTorretas() {
        Map<TipoTorreta, Image> imagenes = new EnumMap<>(TipoTorreta.class);
        imagenes.put(TipoTorreta.SIMPLE, cargar("/assets/torretas/tira_flechas.png"));
        imagenes.put(TipoTorreta.RAPIDA, cargar("/assets/torretas/tira_dardos.png"));
        imagenes.put(TipoTorreta.POTENTE, cargar("/assets/torretas/tira_espadas.png"));
        imagenes.put(TipoTorreta.DISTANTE, cargar("/assets/torretas/tira_flechas_largo_alcanze.png"));
        return imagenes;
    }

    private Map<TipoTorreta, Image[]> cargarProyectiles() {
        Map<TipoTorreta, Image[]> imagenes = new EnumMap<>(TipoTorreta.class);
        imagenes.put(TipoTorreta.SIMPLE, cargarSerie("/assets/proyectiles/flechas/arrow_", ".png"));
        imagenes.put(TipoTorreta.RAPIDA, cargarSerie("/assets/proyectiles/dardos/dart_", ".png"));
        imagenes.put(TipoTorreta.POTENTE, cargarSerie("/assets/proyectiles/espadas/crystal_spear_", ".png"));
        imagenes.put(TipoTorreta.DISTANTE, new Image[]{
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_0_new.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_1.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_2.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_3_new.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_4_new.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_5_new.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_6_new.png"),
                cargar("/assets/proyectiles/felchas_largo_alcanze/javelin_7_new.png")
        });
        return imagenes;
    }

    private Image[] cargarSerie(String prefijo, String sufijo) {
        Image[] imagenes = new Image[8];
        for (int i = 0; i < imagenes.length; i++) {
            imagenes[i] = cargar(prefijo + i + sufijo);
        }
        return imagenes;
    }

    private Image cargar(String ruta) {
        InputStream stream = getClass().getResourceAsStream(ruta);
        if (stream == null) {
            throw new IllegalArgumentException("No se encontro el asset " + ruta);
        }

        return new Image(stream);
    }
}
