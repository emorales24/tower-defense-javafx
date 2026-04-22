# Hoja de ruta del modelo

## Objetivo

Primero dejar un modelo de juego consistente, testeable y desacoplado de JavaFX.
La vista y el `App` se agregan despues, cuando el nucleo del juego ya corra por
si solo con pruebas automatizadas.

## Estado actual

- La estructura de paquetes esta bien orientada: `enemigo`, `torreta`, `nivel`,
  `escena`, `geometria`.
- La unica parte realmente utilizable hoy es `Modelo.geometria`.
- El resto de las clases del dominio estan vacias o apenas declaradas.
- No hay pruebas.
- `pom.xml` y `module-info.java` siguen apuntando a `org.example`, que no existe.

## Que conviene conservar

- `Modelo.geometria.Posicion`
- `Modelo.geometria.Dimension`
- `Modelo.geometria.Caja`
- La separacion conceptual de paquetes del modelo

## Que conviene acomodar antes de seguir

1. Definir un vocabulario estable del dominio.
2. Escribir pruebas del modelo antes de agregar vista.
3. Completar primero un "vertical slice" del juego:
   una base, una ruta, un enemigo, una torreta, un proyectil y un tick de juego.
4. Recién despues pasar a carga de niveles, variedad de enemigos y variedad de torretas.

## Pom y module-info

### `pom.xml`

Mientras el foco sea solo modelo:

- Agregar JUnit 5 para pruebas.
- Agregar `maven-surefire-plugin`.
- Mantener `maven-compiler-plugin`.
- Corregir el `groupId` a uno propio del proyecto.
- Sacar temporalmente el `javafx-maven-plugin` o, como minimo, eliminar la
  referencia a `org.example.App` hasta que exista una clase de arranque real.
- Si dejan JavaFX como dependencia, al menos actualicen la version y no la usen
  como condicion para poder compilar el modelo.

### `module-info.java`

La recomendacion pragmatica es:

- Eliminar `module-info.java` por ahora.

Motivo:

- Hoy no tienen paquetes reales para exportar como modulo.
- La declaracion actual es incorrecta.
- Les agrega friccion innecesaria para desarrollar y testear el modelo.
- JavaFX puede incorporarse despues sin bloquear el avance del dominio.

Si igual quieren mantener JPMS desde ahora, entonces hay que reescribirlo desde
cero con el nombre real del modulo y exportando los paquetes del modelo, no
`org.example`.

## Orden recomendado de implementacion

### Etapa 1: contratos base del modelo

Implementar primero:

- `Actualizable`: `void actualizar(double deltaTime)`
- `Daniable`: operaciones de recibir danio y consultar destruccion
- `Posicionable`: consulta de posicion y/o caja
- `EstadoJuego`
- `EstadoNivel`

Resultado esperado:

- Todos los objetos del dominio comparten contratos claros.

### Etapa 2: escena fija

Implementar:

- `Base`
- `Ruta`
- `Slot`
- `Escenario`

Reglas a cubrir:

- La base tiene vida o integridad.
- La ruta conoce sus puntos y permite avanzar enemigos.
- Los slots saben si se puede construir una torreta.
- El escenario agrupa ruta, base y slots.

Resultado esperado:

- Ya existe un tablero modelado sin vista.

### Etapa 3: enemigos

Implementar:

- `TipoEnemigo`
- `Enemigo`

Reglas a cubrir:

- vida
- velocidad
- recompensa
- danio a la base al llegar
- posicion actual sobre la ruta
- avance por tiempo

Resultado esperado:

- Un enemigo puede spawnear, avanzar y llegar a la base o morir.

### Etapa 4: torretas y proyectiles

Implementar:

- `Torreta`
- `TorretaSimple`
- `TorretaRapida`
- `TorretaPotente`
- `TorretaDistante`
- `TipoTorreta`
- `Proyectil`

Reglas a cubrir:

- costo
- rango
- frecuencia de disparo
- danio
- seleccion de objetivo
- creacion de proyectiles
- impacto y remocion

Resultado esperado:

- Una torreta colocada en un slot puede matar enemigos a traves de ticks.

### Etapa 5: nivel

Implementar:

- `SpawnEnemigos`
- `Nivel`

Reglas a cubrir:

- oleadas
- momento de aparicion de enemigos
- actualizacion de listas activas
- condicion de victoria
- condicion de derrota

Resultado esperado:

- Un nivel completo corre sin interfaz grafica.

### Etapa 6: juego y jugador

Implementar:

- `Jugador`
- `Juego`

Reglas a cubrir:

- dinero
- compra de torretas
- puntaje
- seleccion de nivel actual
- transicion entre estados del juego

Resultado esperado:

- El juego puede correr de punta a punta desde el modelo.

### Etapa 7: tests

Crear `src/test/java` y cubrir primero:

- calculo de distancia y movimiento en geometria
- avance de enemigos por ruta
- llegada a base
- disparo de torretas
- colision de proyectiles
- compra valida e invalida de torretas
- victoria y derrota de nivel

## Orden de trabajo sugerido para las proximas sesiones

1. Limpiar build: corregir `pom.xml` y sacar `module-info.java`.
2. Crear tests base de geometria y contratos.
3. Implementar `Ruta`, `Base`, `Slot`, `Escenario`.
4. Implementar `Enemigo`.
5. Implementar `Torreta` y `Proyectil`.
6. Implementar `Nivel` con spawns.
7. Implementar `Jugador` y `Juego`.
8. Recién ahi integrar JavaFX.

## Primer vertical slice minimo

Si hubiera que arrancar mañana mismo, el mejor recorte es este:

- 1 ruta recta o poligonal simple
- 1 base con vida
- 1 tipo de enemigo
- 1 tipo de torreta
- 1 tipo de proyectil
- 1 nivel con una sola oleada
- 1 jugador con dinero inicial
- tests que demuestren que el nivel se puede ganar y perder

Con eso ya tienen el nucleo del juego. Todo lo demas pasa a ser extension, no
incertidumbre estructural.
