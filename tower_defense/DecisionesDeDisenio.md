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

No se agregaron abstracciones más generales porque todavía no son necesarias, siguiendo KISS y YAGNI.

## Visual
