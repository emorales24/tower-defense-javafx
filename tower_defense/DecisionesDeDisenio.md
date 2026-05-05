# Aca explicamos el porque y como de cada decision de diseño

Principios de diseño: 
- Bajo acoplamiento
- Alta cohesión
- YAGNI
- DRY
- KISS
- SOLID
- TDA, POLK, EDP, POLA, KOP, SOC

Importante: Modelo y Visual tienen que estar completamente desacoplados.

## Modelo

### Geometría del modelo

Se separaron `Posicion`, `Dimension` y `Caja` para que cada clase represente un concepto concreto. Esto apunta a SRP y alta cohesión.

`Posicion` no valida límites del mapa para no acoplarse al escenario. Solo representa un punto.

`Caja` centraliza la lógica de intersección, evitando duplicar cálculos geométricos y respetando DRY y TDA.

### Ruta

`Ruta` representa el camino del nivel: una lista ordenada de posiciones y un ancho lógico. No mueve enemigos ni decide nada de la partida. Solo define por dónde se puede avanzar y qué espacio ocupa ese camino.

Esto separa responsabilidades: la ruta describe el recorrido, mientras que el enemigo decide cómo recorrerlo. La lista se copia al crearse para no depender de cambios externos. Con esto se apunta a SRP, bajo acoplamiento, alta cohesión, KISS y YAGNI.

### Base

`Base` representa solo la base del jugador. La base enemiga no se modela como base porque no tiene vida ni se puede atacar; para eso alcanza con un punto de spawn.

La base guarda posición y vida, y sabe recibir daño. Se trató de mantener a la clase chica, cohesiva y alineada con SRP, DRY, KISS y YAGNI.

### Slot

`Slot` representa el cuadrado donde puede entrar una torreta. Por eso tiene posición, dimensión y caja de colisión. No compra torretas ni decide si el jugador puede instalar algo; eso queda fuera de su responsabilidad.

La idea es que el slot conozca su forma, pero no la lógica del juego. Esto mantiene SRP, alta cohesión y TDA.

### Escenario

`Escenario` agrupa lo estático del nivel: tamaño, ruta, punto de spawn, base del jugador y slots. También valida que el mapa sea coherente, por ejemplo que la ruta empiece en el spawn, termine en la base y que los slots no colisionen con la ruta.

La validación va ahí porque corresponde al mapa, no a las torretas ni al jugador. Así `Nivel` no tiene que arreglar escenarios mal armados mientras corre la partida. Esto defiende SRP, SOC, TDA y bajo acoplamiento.

### Enemigo

`Enemigo` concentra lo común a todos los enemigos: vida, posición, avance por la ruta, daño a la base y recompensas. Los tipos concretos son subclases, para que cada uno defina sus valores sin llenar el código de condicionales.

El enemigo recorre la ruta, pero la ruta no lo mueve. Esto deja clara la separación entre estructura y comportamiento. Se usan `Daniable`, `Actualizable` y `Posicionable` para reutilizar contratos comunes. Acá se buscó polimorfismo, SRP, DRY, TDA y alta cohesión.

### Proyectil

`Proyectil` tiene una responsabilidad bastante puntual: seguir a un objetivo, aplicar daño si impacta y desactivarse cuando ya no sirve. No decide qué enemigo atacar ni maneja el estado del nivel.

La colisión se apoya en `Posicionable` y `Caja`, evitando repetir esa lógica. Esto mantiene SRP, KISS, DRY y TDA.

### Torreta

`Torreta` contiene la lógica común de alcance, cooldown y disparo. Cada tipo concreto hereda de ella y cambia sus valores: costo, daño, rango o cadencia.

La torreta puede elegir un enemigo alcanzable y crear un proyectil, pero no se instala sola ni maneja dinero. Eso queda en `Nivel` y `Jugador`. Con esto se evita mezclar responsabilidades y se usa polimorfismo en vez de condicionales. Principios: SRP, DRY, TDA y alta cohesión.

### Jugador

`Jugador` concentra dinero y puntaje. La idea es que las reglas de recursos no queden repartidas entre torretas, nivel y juego.

Esto evita repetir validaciones de compra o suma de puntaje y mantiene SRP, DRY y alta cohesión.

### SpawnEnemigos

`SpawnEnemigos` representa un enemigo que todavía no apareció. Guarda qué enemigo crear y en qué momento.

Con eso se separa la configuración del nivel de los enemigos activos. `Nivel` coordina el spawn, pero no tiene que mezclar tiempos de aparición con enemigos ya en juego. Esto apunta a SRP, KISS y alta cohesión.

### Nivel

`Nivel` coordina la partida de un nivel: escenario, jugador, spawns, enemigos, torretas y proyectiles. Es la clase que ordena las actualizaciones y aplica consecuencias generales, como recompensas, daño a la base o fin del nivel.

No debería resolver en detalle cómo se mueve un enemigo o cómo impacta un proyectil; eso lo delega. Por eso funciona como coordinador y no como dueño de toda la lógica. Esto apunta a SOC, TDA y bajo acoplamiento.

También se agregaron eventos para enemigos eliminados. La idea es que el modelo pueda informar que un enemigo murió o llegó a la base, pero sin decidir si eso se ve como una explosión, un sonido o cualquier otra cosa. `Nivel` registra el evento y la vista lo consume. Con eso se mantiene la separación entre lógica y presentación.

### Juego

`Juego` queda por encima de `Nivel` y maneja la progresión general: iniciar, pasar de nivel, detectar derrota o victoria final.

La idea es no cargar a `Nivel` con decisiones que pertenecen a la partida completa. `Nivel` resuelve un nivel; `Juego` resuelve el flujo entre niveles. Esto mantiene SOC y bajo acoplamiento.

También se agregó un estado intermedio, `VICTORIA_NIVEL`. La razón es que el juego no debería pasar automáticamente al siguiente nivel apenas detecta que se ganó. Primero se informa la victoria y recién después se avanza. Esto hace que el flujo del programa coincida mejor con lo que pasa para el jugador y evita mezclar la lógica interna con el momento en el que la vista muestra el cartel.

## Carga

### Carga de niveles

La carga de niveles quedó separada del modelo. `CargadorNivelXml` lee el XML, lo valida contra el XSD y recién después construye los objetos del modelo: ruta, escenario, slots, enemigos pendientes, torretas iniciales y nivel.

La idea es que el XML defina la composición del nivel, pero no que el modelo sepa leer archivos. Con eso `Modelo` no depende de `Carga`, ni de XML, ni de JavaFX. Esto mantiene SOC, bajo acoplamiento y EDP.

También se dejó una excepción propia, `CargaNivelException`, para que los errores de archivo queden agrupados y sean más claros. Si el XML está mal formado o semánticamente no cierra, la carga falla antes de que el juego empiece.

### Fábricas

Las fábricas de enemigos y torretas traducen un tipo del modelo a una clase concreta. Por ejemplo, `DEBIL` termina creando un `EnemigoDebil`, y `SIMPLE` una `TorretaSimple`.

Esto apareció porque el XML no debería conocer clases Java concretas. El XML dice qué tipo quiere; el modelo decide cómo construirlo. Así se evita repartir condicionales por `Nivel` o por la carga. El `switch` queda concentrado en una clase cuya responsabilidad es justamente crear objetos.

### Carga de juego

`CargadorJuegoXml` usa el cargador de niveles para armar el juego completo. Crea un único `Jugador`, carga los tres niveles con ese mismo jugador y devuelve un `Juego` ya listo para iniciar.

La decisión busca que más adelante la vista no tenga que saber qué XML existen ni cómo se arma cada nivel. La vista debería pedir un `Juego` cargado y usarlo. Esto mantiene separada la construcción inicial del juego de la interfaz y evita duplicar esa lógica.

En los niveles, las torretas iniciales quedaron como algo opcional del XML. Si aparece la sección vacía, el nivel empieza sin torretas. Esto es compatible con el enunciado, porque dice que el nivel puede traer torretas preinstaladas, no que siempre tenga que hacerlo.

## Visual

La parte visual quedó separada del modelo. La vista usa los objetos del modelo para consultar qué tiene que dibujar o qué acción pedir, pero el modelo no conoce JavaFX ni depende de clases visuales.

`App` funciona como punto de entrada de JavaFX y maneja el cambio entre menú y juego. También se encarga de mostrar los carteles de victoria, derrota o error de carga. Esto evita que `Juego` o `Nivel` tengan que saber cómo se muestra algo en pantalla.

`VistaMenu` representa el menú principal y la selección de niveles. Los niveles se van desbloqueando desde la vista porque eso por ahora es una decisión de navegación, no una regla interna del modelo. También se le dio estilo propio, pero sin mezclarlo con reglas del juego.

`VistaJuego` coordina la pantalla de juego. No dibuja todo directamente, sino que delega la barra superior en `BarraJuego`, la selección de torretas en `SelectorTorretas` y el dibujo del escenario en `RenderizadorJuego`. Esto se hizo para que la clase no concentre interfaz, renderizado, clicks y flujo de partida al mismo tiempo.

`BarraJuego` quedó para información y acciones generales: nivel, dinero, puntaje, audio, reinicio y menú. La selección de torretas se bajó a `SelectorTorretas` para que no compita por espacio con el puntaje y los botones. Además el selector usa íconos, costos y tooltips, lo cual hace más clara la acción sin agregar lógica al modelo.

La instalación de torretas sigue siendo con click sobre un slot. El hover solo cambia la selección o muestra una previsualización. Se decidió así para evitar gastar dinero por accidente al mover el mouse. Es una decisión de interfaz, no de modelo.

`AssetsJuego` centraliza la carga de imágenes. La idea es no repartir rutas de archivos por todas las clases visuales. El renderizador pide el asset que necesita y no tiene que saber dónde está guardado. Esto respeta DRY y mantiene más bajo el acoplamiento dentro de la vista.

`RenderizadorJuego` dibuja el estado actual del modelo: fondo, ruta, slots, base, torretas, enemigos, proyectiles y explosiones. Usa el tipo de torreta o enemigo para elegir el sprite, pero no decide reglas de juego. También dibuja la previsualización de la torreta seleccionada cuando el mouse está sobre un slot libre.

Las explosiones y sonidos se disparan desde eventos del modelo. `Nivel` no sabe que hay una animación ni un audio; solo informa que un enemigo fue eliminado. La vista decide cómo representarlo. Esto mantiene SOC y evita meter JavaFX o recursos visuales dentro del modelo.

`AudioJuego` quedó como parte de la vista. Maneja música, efectos y mute, pero el modelo no depende de eso. Para el mute se decidió cortar la reproducción actual y volver a iniciarla al desmutear, porque con `AudioClip` cambiar el volumen no siempre afectaba bien la música ya iniciada.

El loop se separó en dos partes: uno actualiza el modelo con un paso fijo y otro renderiza la pantalla. La idea es que la lógica del juego no dependa de cuánto tarde cada frame en dibujarse. Esto se acerca más a lo pedido por el enunciado y mantiene más clara la diferencia entre cómputo y presentación.

Cuando se gana un nivel, la vista frena los loops, muestra el cartel y después le pide a `Juego` avanzar. Con eso el orden queda más natural: primero victoria, después siguiente nivel. Además se evita que el `AnimationTimer` quede bloqueado por el cartel.

En esta parte se buscó sobre todo SOC y bajo acoplamiento: la vista presenta y pide acciones, pero no define las reglas del juego. También se mantuvo KISS, separando en clases nuevas solo cuando empezó a haber una responsabilidad clara: render, audio, assets, barra y selector.
