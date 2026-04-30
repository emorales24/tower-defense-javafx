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

### Juego

`Juego` queda por encima de `Nivel` y maneja la progresión general: iniciar, pasar de nivel, detectar derrota o victoria final.

La idea es no cargar a `Nivel` con decisiones que pertenecen a la partida completa. `Nivel` resuelve un nivel; `Juego` resuelve el flujo entre niveles. Esto mantiene SOC y bajo acoplamiento.

## Visual
