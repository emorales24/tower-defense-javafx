# FIUBA - Paradigmas de Programación

## Tower Defense - Trabajo Práctico 1

### Docentes
- **Jefe de Cátedra:** Diego Essaya  
- **Jefe de Trabajos Prácticos:** Santiago Maraggi
- **Corrector:** ...  

### Integrantes del Grupo
- Eduardo José Morales Depablos 113860 [vídeo]() agregar
- Fausto Giannotti 113365 [vídeo](https://drive.google.com/file/d/1sQXP9Iv9a_LlwrfNxOJSJyUN4Nx3mFgU/view?usp=drive_link)

### Descripción del Proyecto
Este proyecto es un Tower Defense hecho en Java y JavaFX como Trabajo Práctico 1 de la materia Paradigmas de Programación.

El juego tiene 3 niveles. En cada nivel los enemigos aparecen desde un punto de spawn, avanzan por una ruta fija y tratan de llegar a la base del jugador. El jugador puede instalar torretas en slots predefinidos para destruirlos antes de que lleguen.

El proyecto está separado en capas:

- `Modelo`: lógica del juego, enemigos, torretas, proyectiles, niveles, escenario y jugador.
- `Carga`: lectura y validación de niveles desde XML/XSD.
- `Vista`: interfaz JavaFX, renderizado, menú, sonidos y assets.

### Instalación
Requisitos:

- Java 11 o superior.
- Maven.
- Git.

Para clonar el repositorio:

```bash
git clone git@github.com:paradigmas-tb025-essaya/tp1-2026c1-morales-giannotti.git
cd tp1-2026c1-morales-giannotti/tower_defense
```

Para compilar y correr los tests:

```bash
mvn test
```

### Uso
Para ejecutar el juego, utilice el siguiente comando:

```bash
mvn javafx:run
```

El comando debe ejecutarse desde la carpeta `tower_defense`, donde está el archivo `pom.xml`.

### Cómo jugar

Desde el menú principal se puede seleccionar un nivel desbloqueado o salir del juego. Al principio solo está disponible el nivel 1. Los niveles siguientes se desbloquean al ganar.

En la pantalla de juego:

- La barra superior muestra nivel actual, dinero disponible, puntaje, audio, reinicio y vuelta al menú.
- La barra inferior permite elegir torretas.
- Pasar el mouse por una torreta del selector la deja seleccionada.
- Pasar el mouse por un slot libre muestra una previsualización de la torreta seleccionada.
- Hacer click sobre un slot instala la torreta si hay dinero suficiente.
- Si el slot ya tenía una torreta, se reemplaza comprando la nueva.

Tipos de torretas:

- `Simple`: torreta base.
- `Rapida`: dispara con mayor cadencia.
- `Potente`: hace más daño por disparo.
- `Distante`: tiene mayor alcance.

El nivel termina cuando:

- Se destruyen todos los enemigos.
- La base del jugador se queda sin vida.
- El jugador vuelve al menú o cierra la aplicación.

### Formato de niveles

Los niveles están definidos en:

```text
tower_defense/src/main/resources/niveles/
```

Cada XML define:

- tamaño del escenario
- dinero inicial
- punto de spawn
- base del jugador
- ruta
- slots para torretas
- enemigos y tiempo de aparición
- torretas iniciales opcionales

Ejemplo resumido:

```xml
<nivel ancho="800" alto="600" dineroInicial="200">
    <spawn x="0" y="300"/>
    <baseJugador x="760" y="120" vida="20"/>

    <ruta ancho="32">
        <punto orden="1" x="0" y="300"/>
        <punto orden="2" x="250" y="300"/>
    </ruta>

    <slots>
        <slot x="120" y="240" ancho="32" alto="32"/>
    </slots>

    <enemigos>
        <enemigo tipo="DEBIL" tiempo="0"/>
    </enemigos>

    <torretasIniciales/>
</nivel>
```

El formato se valida con:

```text
tower_defense/src/main/resources/niveles/nivel.xsd
```

Si el XML no respeta el formato o define un nivel inválido, el programa muestra un error y no inicia el juego.

### Licencias

- **Dungeon Crawl Tiles:** [Dungeon Crawl 32x32 Tiles](https://opengameart.org/content/dungeon-crawl-32x32-tiles) - Public Domain / CC0. 
Autor: Chris Hamons. https://opengameart.org/content/dungeon-crawl-32x32-tiles

- **Canción de Menú:** [Crystal Cave Song](https://opengameart.org/content/crystal-cave-song18) - Public Domain / CC0. Atribución: Créditos a [pixelsphere.org](https://pixelsphere.org) / The Cynic Project. 

- **Canción de Juego:** [Space Boss Battle Theme](https://opengameart.org/content/space-boss-battle-theme) - CC-BY 3.0. Autor: Matthew Pablo. http://www.matthewpablo.com. Usuario en OpenGameArt: https://opengameart.org/users/matthewpablo

- **Efectos de Sonido:** [8-Bit Platformer SFX](https://opengameart.org/content/8-bit-platformer-sfx) - CC-BY 3.0. Autor: bart.
