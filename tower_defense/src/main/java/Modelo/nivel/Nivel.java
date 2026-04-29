package Modelo.nivel;

import Modelo.Actualizable;
import Modelo.Jugador;
import Modelo.enemigo.Enemigo;
import Modelo.escena.Base;
import Modelo.escena.Escenario;
import Modelo.escena.Slot;
import Modelo.proyectil.Proyectil;
import Modelo.torreta.TipoTorreta;
import Modelo.torreta.Torreta;
import Modelo.torreta.TorretaDistante;
import Modelo.torreta.TorretaPotente;
import Modelo.torreta.TorretaRapida;
import Modelo.torreta.TorretaSimple;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Nivel {
    private final Escenario escenario;
    private final Jugador jugador;
    private final double dineroInicial;
    private final List<SpawnEnemigos> spawnsConfigurados;
    private final Map<Slot, TipoTorreta> torretasInicialesConfiguradas;

    private final List<Enemigo> enemigosActivos;
    private final List<Proyectil> proyectilesActivos;
    private final Map<Slot, Torreta> torretasInstaladas;
    private final List<SpawnEnemigos> spawnsPendientes;

    private double tiempoTranscurrido;
    private EstadoNivel estado;

    public Nivel(
            Escenario escenario,
            Jugador jugador,
            double dineroInicial,
            List<SpawnEnemigos> spawnsConfigurados,
            Map<Slot, Torreta> torretasIniciales
    ) {
        if (escenario == null || jugador == null || spawnsConfigurados == null || torretasIniciales == null) {
            throw new IllegalArgumentException("El nivel requiere escenario, jugador, spawns y torretas iniciales");
        }

        if (Double.isNaN(dineroInicial) || Double.isInfinite(dineroInicial) || dineroInicial < 0) {
            throw new IllegalArgumentException("El dinero inicial debe ser no negativo y finito");
        }

        this.escenario = escenario;
        this.jugador = jugador;
        this.dineroInicial = dineroInicial;
        this.spawnsConfigurados = List.copyOf(spawnsConfigurados);
        this.torretasInicialesConfiguradas = convertirTorretasIniciales(torretasIniciales);
        this.enemigosActivos = new ArrayList<>();
        this.proyectilesActivos = new ArrayList<>();
        this.torretasInstaladas = new LinkedHashMap<>();
        this.spawnsPendientes = new ArrayList<>();
        this.estado = EstadoNivel.NO_INICIADO;

        validarTorretasIniciales();
    }

    public void iniciar() {
        enemigosActivos.clear();
        proyectilesActivos.clear();
        torretasInstaladas.clear();
        cargarTorretasIniciales();
        spawnsPendientes.clear();
        spawnsPendientes.addAll(spawnsConfigurados);
        escenario.obtenerBaseEnemiga().reiniciarVida();
        escenario.obtenerBaseJugador().reiniciarVida();
        jugador.establecerDineroDisponible(dineroInicial);
        tiempoTranscurrido = 0;
        estado = EstadoNivel.EN_CURSO;
    }

    public void actualizar(double deltaTiempo) {
        Actualizable.validarDeltaTiempo(deltaTiempo);

        if (estado != EstadoNivel.EN_CURSO) {
            return;
        }

        tiempoTranscurrido += deltaTiempo;

        aparecerEnemigosPendientes();
        actualizarEnemigos(deltaTiempo);
        actualizarTorretas(deltaTiempo);
        actualizarProyectiles(deltaTiempo);
        procesarConsecuencias();
        verificarEstado();
    }

    public boolean instalarTorreta(Slot slot, TipoTorreta tipoTorreta) {
        if (slot == null || tipoTorreta == null) {
            throw new IllegalArgumentException("El slot y el tipo de torreta no pueden ser null");
        }

        if (estado != EstadoNivel.EN_CURSO) {
            return false;
        }

        if (!escenario.contiene(slot)) {
            throw new IllegalArgumentException("El slot no pertenece al escenario");
        }

        Torreta nuevaTorreta = crearTorreta(tipoTorreta, slot);

        if (!jugador.puedeComprar(nuevaTorreta.obtenerCosto())) {
            return false;
        }

        jugador.descontarDinero(nuevaTorreta.obtenerCosto());
        torretasInstaladas.put(slot, nuevaTorreta);
        return true;
    }

    public Escenario obtenerEscenario() {
        return escenario;
    }

    public Jugador obtenerJugador() {
        return jugador;
    }

    public List<Enemigo> obtenerEnemigosActivos() {
        return List.copyOf(enemigosActivos);
    }

    public List<Proyectil> obtenerProyectilesActivos() {
        return List.copyOf(proyectilesActivos);
    }

    public Map<Slot, Torreta> obtenerTorretasInstaladas() {
        return Map.copyOf(torretasInstaladas);
    }

    public List<SpawnEnemigos> obtenerSpawnsPendientes() {
        return List.copyOf(spawnsPendientes);
    }

    public double obtenerTiempoTranscurrido() {
        return tiempoTranscurrido;
    }

    public EstadoNivel obtenerEstado() {
        return estado;
    }

    private void aparecerEnemigosPendientes() {
        List<SpawnEnemigos> spawnsAEliminar = new ArrayList<>();

        for (SpawnEnemigos spawn : spawnsPendientes) {
            if (spawn.debeAparecer(tiempoTranscurrido)) {
                enemigosActivos.add(spawn.crearEnemigo(escenario.obtenerRuta()));
                spawnsAEliminar.add(spawn);
            }
        }

        spawnsPendientes.removeAll(spawnsAEliminar);
    }

    private void actualizarEnemigos(double deltaTiempo) {
        for (Enemigo enemigo : enemigosActivos) {
            enemigo.actualizar(deltaTiempo);
        }
    }

    private void actualizarTorretas(double deltaTiempo) {
        for (Torreta torreta : torretasInstaladas.values()) {
            torreta.actualizar(deltaTiempo);
            Optional<Proyectil> disparo = torreta.dispararSiPuede(enemigosActivos);
            disparo.ifPresent(proyectilesActivos::add);
        }
    }

    private void actualizarProyectiles(double deltaTiempo) {
        for (Proyectil proyectil : proyectilesActivos) {
            proyectil.actualizar(deltaTiempo);
        }
    }

    private void procesarConsecuencias() {
        Base baseJugador = escenario.obtenerBaseJugador();
        List<Enemigo> enemigosAEliminar = new ArrayList<>();

        for (Enemigo enemigo : enemigosActivos) {
            if (enemigo.estaActivo()) {
                continue;
            }

            if (enemigo.llegoAlFinal()) {
                baseJugador.recibirDanio(enemigo.obtenerDanioABase());
            } else if (enemigo.estaDestruido()) {
                jugador.sumarDinero(enemigo.obtenerRecompensaDinero());
                jugador.sumarPuntaje(enemigo.obtenerRecompensaPuntaje());
            }

            enemigosAEliminar.add(enemigo);
        }

        enemigosActivos.removeAll(enemigosAEliminar);
        proyectilesActivos.removeIf(
                proyectil -> !proyectil.estaActivo() || !escenario.contiene(proyectil.obtenerPosicion())
        );
    }

    private void verificarEstado() {
        if (escenario.obtenerBaseJugador().estaDestruido()) {
            estado = EstadoNivel.PERDIDO;
            return;
        }

        if (spawnsPendientes.isEmpty() && enemigosActivos.isEmpty()) {
            estado = EstadoNivel.GANADO;
        }
    }

    private void validarTorretasIniciales() {
        for (Slot slot : torretasInicialesConfiguradas.keySet()) {
            if (!escenario.contiene(slot)) {
                throw new IllegalArgumentException("Todas las torretas iniciales deben estar en slots del escenario");
            }
        }
    }

    private Map<Slot, TipoTorreta> convertirTorretasIniciales(Map<Slot, Torreta> torretasIniciales) {
        Map<Slot, TipoTorreta> tipos = new LinkedHashMap<>();

        for (Map.Entry<Slot, Torreta> entrada : torretasIniciales.entrySet()) {
            if (entrada.getKey() == null || entrada.getValue() == null) {
                throw new IllegalArgumentException("Las torretas iniciales no pueden tener claves o valores null");
            }

            tipos.put(entrada.getKey(), entrada.getValue().obtenerTipo());
        }

        return Map.copyOf(tipos);
    }

    private void cargarTorretasIniciales() {
        for (Map.Entry<Slot, TipoTorreta> entrada : torretasInicialesConfiguradas.entrySet()) {
            torretasInstaladas.put(entrada.getKey(), crearTorreta(entrada.getValue(), entrada.getKey()));
        }
    }

    private Torreta crearTorreta(TipoTorreta tipoTorreta, Slot slot) {
        switch (tipoTorreta) {
            case SIMPLE:
                return new TorretaSimple(slot.obtenerPosicion());
            case RAPIDA:
                return new TorretaRapida(slot.obtenerPosicion());
            case POTENTE:
                return new TorretaPotente(slot.obtenerPosicion());
            case DISTANTE:
                return new TorretaDistante(slot.obtenerPosicion());
            default:
                throw new IllegalStateException("Tipo de torreta no soportado");
        }
    }
}
