## Integrante 1 - TDAs y grafo base

Se implementaron las estructuras de datos propias necesarias para el núcleo algorítmico del proyecto:

- `MyLinkedList<T>`: lista enlazada simple genérica.
- `MyQueue<T>`: cola FIFO basada en nodos.
- `MyStack<T>`: pila LIFO basada en nodos.
- `MyPriorityQueue<T>`: cola de prioridad mínima basada en heap binario.
- `UnionFind`: estructura de conjuntos disjuntos para Kruskal.

También se implementó el TDA `Graph`, que representa un grafo no dirigido y ponderado mediante lista de adyacencia.

### Complejidades principales

| Estructura | Operación | Complejidad |
|---|---:|---:|
| MyLinkedList | add | O(n) |
| MyLinkedList | addFirst | O(1) |
| MyLinkedList | get | O(n) |
| MyQueue | enqueue | O(1) |
| MyQueue | dequeue | O(1) |
| MyStack | push | O(1) |
| MyStack | pop | O(1) |
| MyPriorityQueue | insert | O(log n) |
| MyPriorityQueue | extractMin | O(log n) |
| MyPriorityQueue | decreasePriority | O(n) + O(log n) |
| UnionFind | find | casi O(1) amortizado |
| UnionFind | union | casi O(1) amortizado |
| Graph | addVertex | O(V) |
| Graph | addEdge | O(V) |
| Graph | getWeight | O(grado del vértice) |

## Integrante 2 - Algoritmos de Recorridos y Caminos Mínimos

Se diseñó e implementó el componente lógico y algorítmico central del sistema (`GraphAlgorithms`), encargado de la exploración de la red vial, la validación de conectividad logística y la optimización de rutas de entrega.

- **Recorridos de Red Vial**: Implementación de `BFS` (amplitud) y `DFS` (profundidad) para análisis de cobertura.
- **Análisis de Cierre Transitivo**: Implementación del algoritmo de `Warshall` adaptado para validar de forma automática si todos los puntos de entrega y clientes son alcanzables desde el depósito central.
- **Optimización de Rutas Punto a Punto**: Implementación del algoritmo de `Dijkstra` utilizando la cola de prioridad, diseñado con un mecanismo de rastreo inverso para reconstruir el camino óptimo paso a paso (camino y distancia).
- **Matriz de Conectividad Total**: Implementación del algoritmo de `Floyd-Warshall` para calcular y precomputar las distancias mínimas absolutas entre todos los pares posibles de ubicaciones en la ciudad.

### Complejidades de los Algoritmos Implementados

| Algoritmo / Operación | Propósito Logístico | Complejidad Temporal | Complejidad Espacial |
|---|---|:---:|:---:|
| `bfs(Graph, startId)` | Exploración por capas del mapa vial | O(V + E) | O(V) |
| `dfs(Graph, startId)` | Exploración a profundidad de rutas alternativas | O(V + E) | O(V) (Call Stack) |
| `warshall(Graph, depotId)` | Verificación de alcanzabilidad desde el depósito | O(V³) | O(V²) |
| `dijkstra(Graph, startId)` | Reconstrucción del camino más corto por metros | O(V² + E log V) | O(V) |
| `floydWarshall(Graph)` | Generación de la matriz de distancias globales | O(V³) | O(V²) |

## Integrante 3 - MST, Asignación de Paquetes y Heurísticas de Rutas

Esta sección del proyecto LogísTEC abarca la optimización logística mediante la construcción de árboles de expansión mínima (MST), la asignación de paquetes a camiones y la planificación de rutas de entrega utilizando heurísticas.

El trabajo se apoya en el grafo del proyecto:  
- Los vértices representan puntos de la ciudad.  
- Las aristas representan calles con distancias en metros.  
- Todas las estructuras algorítmicas se implementaron usando arreglos nativos y estructuras propias del equipo, sin colecciones de `java.util`.

---

## Clases y algoritmos implementados

### Paquete `algorithms`

- **MSTResult**: Almacena las aristas seleccionadas por Prim o Kruskal, costo total y tiempo de ejecución.

- **MSTAlgorithms**: Implementación de Prim y Kruskal, métodos auxiliares para obtener aristas únicas, ordenar aristas y agregar aristas candidatas.  
  Incluye comparación empírica de costos y tiempos.  
- Método adicional: `comparePrimAndKruskal` para comparar visualmente Prim y Kruskal y verificar que produzcan el mismo costo total.

### Paquete `model`

- **Package**: Representa un paquete con identificador, destino, peso y prioridad.  
- **Truck**: Representa un camión con capacidad máxima, carga actual, paquetes asignados y métodos para verificar si un paquete cabe y agregarlo.  
- **AssignmentResult**: Guarda los paquetes rechazados y los camiones con sus paquetes asignados.  
- **RouteResult**: Almacena el resultado de una ruta, incluyendo heurística utilizada, paradas ordenadas, distancia total y depósito.

### Paquete `planner`

- **PackageAssigner**: asigna paquetes a camiones siguiendo la heurística indicada por el proyecto:  
  1. Ordenar por prioridad ascendente.  
  2. Desempate por peso descendente.  
  3. Asignar al camión con mayor capacidad libre.  
  4. Paquetes que no caben se marcan como rechazados.  
  > Nota: Aunque se menciona "best-fit", se sigue la descripción textual del proyecto.  
        >Camión con mayor capacidad libre disponible.

- **RoutePlanner**: Planificación de rutas con:  
  - Nearest Neighbor  
  - MST-Based  
  - Métodos auxiliares:
    - Obtener paradas únicas de un camión  
    - Construir MST inducido  
    - DFS preorden  
    - Calcular distancia total  
    - Comparar rutas  
    - Generar reporte final por camión

### Cambios en `GraphAlgorithms.java`

- Se agregó el método `getFloydWarshallDistanceMatrix(Graph graph)` que retorna la matriz de distancias mínimas entre todos los pares de vértices.  
- Esto permite que las heurísticas de ruta puedan consultar distancias sin depender de la impresión en consola.

---

## Funcionalidades y complejidades principales

| Funcionalidad / Método | Propósito | Complejidad Temporal | Complejidad Espacial |
|------------------------|-----------|-------------------|-------------------|
| Kruskal (MSTAlgorithms) | Construcción de MST revisando aristas ordenadas y evitando ciclos con UnionFind | O(E²) aprox. | O(E + V) |
| Prim (MSTAlgorithms) | Construcción de MST desde vértice inicial usando cola de prioridad | O(E log E) aprox. | O(V + E) |
| Comparación Prim vs Kruskal | Comparar costos y tiempos de ejecución | O(E²) | O(E + V) |
| sortPackagesByPriorityAndWeight | Ordenar paquetes por prioridad y peso | O(P²) | O(P) |
| assignPackages (PackageAssigner) | Asignar paquetes a camiones según capacidad y regla del enunciado | O(P × C) | O(P + C) |
| getUniqueStops (RoutePlanner) | Obtener destinos únicos de un camión | O(P × S) | O(S) |
| nearestNeighbor (RoutePlanner) | Planificación de ruta por Nearest Neighbor | O(S²) | O(S) |
| mstBasedRoute (RoutePlanner) | Heurística MST-Based con DFS sobre MST inducido | O(S² + A²) | O(S + A) |
| calculateRouteDistance (RoutePlanner) | Calcula distancia total de una ruta | O(S) | O(S) |
| printFinalTruckReport | Genera reporte final por camión con rutas, carga y ahorro | O(S² + A²) | O(S + A) |

**Notación:**

- V = cantidad de vértices del grafo  
- E = cantidad de aristas del grafo  
- P = cantidad de paquetes  
- C = cantidad de camiones  
- S = cantidad de paradas únicas por camión  
- A = cantidad de aristas del MST inducido para MST-Based

---

## Observaciones

- Se reutilizaron las estructuras del equipo: `MyPriorityQueue`, `UnionFind` y `MyLinkedList`.  
- Todos los algoritmos y heurísticas funcionan con arreglos nativos y estructuras propias, sin usar colecciones de `java.util`.  
- La asignación de paquetes sigue la regla textual del proyecto: se asigna al camión con mayor capacidad libre que pueda alojar el paquete.  
- Las rutas calculadas se pueden usar para impresión en consola o integración con la interfaz gráfica.  
- La matriz de Floyd-Warshall se retornó para que RoutePlanner pueda consultar distancias mínimas sin depender de la impresión.

### Mejora en MST para grafos no conexos

Se mejoró el manejo de Prim y Kruskal cuando el grafo no es conexo.

Anteriormente, si el grafo tenía más de una componente conexa, Prim podía recorrer únicamente la componente alcanzable desde el vértice inicial. Esto provocaba que la comparación con Kruskal pudiera mostrar costos diferentes, aunque el problema real era que no existía un MST completo.

Con la mejora implementada:

* `MSTResult` ahora guarda la cantidad de componentes conexas detectadas.
* `MSTResult` permite identificar si el resultado corresponde a un MST completo o a un bosque de expansión mínima.
* `Prim` procesa primero el vértice inicial indicado y luego continúa con los demás vértices no visitados.
* Si el grafo no es conexo, `Prim` genera un bosque de expansión mínima.
* `Kruskal` cuenta las componentes finales mediante `UnionFind`.
* `ReportGenerator` distingue entre:

  * MST completo, cuando el grafo es conexo.
  * Bosque de expansión mínima, cuando el grafo no es conexo.
* La comparación Prim vs Kruskal ahora reporta correctamente si ambos algoritmos obtienen el mismo costo de MST o el mismo costo de bosque mínimo.

Esta mejora permite que el sistema responda correctamente ante casos donde existen vértices o paquetes en componentes desconectadas del depósito.

## Integrante 4 - JSON, Integración, Interfaz Gráfica y README

Esta sección del proyecto LogísTEC se encarga de la carga de datos desde archivos JSON, la integración general de los módulos desarrollados por los demás integrantes, la interfaz gráfica en JavaFX y la documentación de ejecución del sistema.

El trabajo permite que el proyecto se pueda ejecutar desde una pantalla principal, seleccionar distintos casos de prueba, visualizar el grafo, consultar caminos mínimos, revisar rutas de camiones y cargar archivos JSON externos para pruebas adicionales.

---

## Clases implementadas e integradas

### Paquete `io`

* **LogisticsData**: Clase contenedora que agrupa los datos principales del caso cargado: grafo, depósito, paquetes y camiones.
  Esta clase facilita el paso de información entre la carga JSON, el reporte, los algoritmos y la interfaz gráfica.

* **JsonLoader**: Clase encargada de leer los archivos `.json` del proyecto utilizando Gson.
  Construye los objetos necesarios del sistema a partir del archivo de entrada:

  * Vértices.
  * Aristas.
  * Depósito.
  * Paquetes.
  * Camiones.

  También realiza validaciones básicas, como verificar que exista un depósito, que los destinos de los paquetes existan en el grafo y que los pesos, prioridades y capacidades sean válidos.

* **ReportGenerator**: Clase encargada de integrar la ejecución del reporte en consola.
  Muestra información general del caso, conectividad, paquetes no asignados, MST, asignación de paquetes y rutas calculadas.

### Paquete `ui`

* **MainWindow**: Controla la ventana principal del sistema.
  Permite seleccionar un caso JSON, cargar casos externos, visualizar el grafo, volver a la pantalla principal y abrir el panel completo de información.

* **GraphPane**: Dibuja el grafo en pantalla usando JavaFX.
  Representa visualmente:

  * Depósito.
  * Casas o puntos de entrega.
  * Intersecciones.
  * Aristas.
  * MST.
  * Rutas de camiones.
  * Paquetes no asignados.
  * Camino mínimo consultado con Dijkstra.

* **InfoPanel**: Muestra el panel completo de información del caso.
  Incluye resumen, consulta de camino mínimo, MST, camiones, comparación de heurísticas y paquetes no asignados.

* **LegendPanel**: Muestra la información relevante del grafo en el panel lateral derecho.
  Se utiliza como leyenda visual para no saturar la pantalla principal.

### Paquete `model`

* **ShortestPathResult**: Guarda el resultado de una consulta específica de Dijkstra.
  Almacena el origen, destino, camino mínimo, distancia total y si el destino es alcanzable.

* **RouteComparisonResult**: Guarda la comparación entre las heurísticas de ruta:

  * Nearest Neighbor.
  * MST-Based.

  También almacena cuál ruta fue seleccionada como recomendada y el porcentaje de ahorro de MST-Based respecto a Nearest Neighbor.

---

## Casos JSON implementados

Los archivos de prueba se encuentran en la carpeta:

```text
data/
```

Se agregaron los siguientes casos:

* **caso_pequeno.json**: Caso reducido para pruebas rápidas.
  Contiene 8 vértices, 12 aristas, 5 paquetes y 2 camiones.

* **caso_medio.json**: Caso intermedio para probar rutas, MST, asignación y visualización.
  Contiene 15 vértices, 25 aristas, 10 paquetes y 3 camiones.

* **caso_minimo.json**: Caso principal obligatorio del proyecto.
  Contiene 30 vértices, 50 aristas, 15 paquetes y 3 camiones.

* **caso_inaccesible.json**: Caso especial para probar destinos no alcanzables desde el depósito.
  Contiene paquetes dirigidos a vértices que pertenecen a una componente desconectada del grafo.

---

## Funcionalidades implementadas

### Selección de casos desde la interfaz

La pantalla principal permite seleccionar el caso JSON que se desea visualizar.
Si el usuario presiona el botón de visualizar sin haber seleccionado un caso, el sistema muestra un mensaje indicando que primero debe seleccionar uno.

También se agregó la opción de cargar un archivo JSON externo mediante un selector de archivos. Esto permite que el profesor pueda probar nuevos casos sin modificar el código fuente.

### Visualización gráfica del grafo

La interfaz muestra el grafo de la ciudad con una representación visual diferenciada:

* Depósito.
* Casas o entregas.
* Intersecciones.
* Aristas.
* Rutas de camiones.
* MST.
* Paquetes no asignados.

Además, se mantiene una leyenda lateral para identificar los colores y símbolos utilizados.

### Consulta visual de Dijkstra

Se agregó una consulta visual de camino mínimo.
El usuario puede seleccionar un origen y un destino, presionar el botón de cálculo y obtener:

* Camino mínimo.
* Distancia total.
* Resaltado visual del camino en el grafo.

Esto permite demostrar de forma gráfica el uso de Dijkstra dentro del sistema.

### Comparación de heurísticas

En el panel completo se muestra la comparación entre:

* Nearest Neighbor.
* MST-Based.

Para cada camión se muestra:

* Distancia de cada heurística.
* Tiempo estimado de recorrido.
* Heurística seleccionada.
* Ruta recomendada.
* Porcentaje de ahorro.

### Paquetes no asignados

El sistema muestra los paquetes que no fueron asignados a ningún camión.
Un paquete puede quedar no asignado por dos razones:

* Capacidad insuficiente en la flota.
* Destino inalcanzable desde el depósito.

La validación de destinos inalcanzables se realiza utilizando la matriz de alcanzabilidad generada con Warshall.

### Botón para volver al inicio

Se agregó un botón para volver a la pantalla principal después de visualizar un caso.
Esto permite probar otro caso sin cerrar y volver a ejecutar el programa.

### Panel completo de información

Se agregó un botón llamado `Ver panel completo`, que abre una ventana adicional con toda la información del caso seleccionado.
Esto evita saturar la pantalla principal y permite revisar el reporte visual de forma más ordenada.

---

## Integración general del sistema

El flujo general del sistema es el siguiente:

```text
1. Ejecutar app.Main.
2. Mostrar pantalla principal.
3. Seleccionar un caso JSON o cargar un caso externo.
4. Cargar datos con JsonLoader.
5. Guardar datos en LogisticsData.
6. Generar reporte en consola con ReportGenerator.
7. Asignar paquetes a camiones.
8. Calcular MST y rutas.
9. Mostrar el grafo en JavaFX.
10. Permitir consultas visuales y revisión del panel completo.
```

Es importante mantener el orden de integración, ya que la asignación de paquetes debe ejecutarse antes de mostrar las rutas en la interfaz.

---

## Dependencias necesarias

Para ejecutar correctamente el proyecto se necesitan las siguientes dependencias:

* Java 17 o superior.
* JavaFX.
* Gson.

### Configuración de JavaFX

En IntelliJ IDEA se debe agregar JavaFX como librería externa y configurar las VM Options.

Ejemplo de VM Options:

```text
--module-path "RUTA_A_JAVAFX\lib" --add-modules javafx.controls,javafx.fxml
```

Ejemplo usado durante el desarrollo:

```text
--module-path "C:\Universidad\javafx 21\javafx-sdk-21.0.10\lib" --add-modules javafx.controls,javafx.fxml
```

La ruta puede cambiar dependiendo de la computadora donde se ejecute el proyecto.

### Configuración de Gson

Gson debe agregarse como librería externa en IntelliJ IDEA.
Se utilizó el archivo:

```text
gson-2.10.1.jar
```

Para agregarlo:

1. Ir a `File`.
2. Entrar en `Project Structure`.
3. Seleccionar `Libraries`.
4. Presionar `+`.
5. Seleccionar `Java`.
6. Buscar el archivo `.jar` de Gson.
7. Aplicar los cambios.

---

## Cómo ejecutar el proyecto

Para ejecutar el sistema:

1. Abrir el proyecto en IntelliJ IDEA.
2. Verificar que el JDK esté configurado.
3. Agregar JavaFX como librería.
4. Agregar Gson como librería.
5. Configurar las VM Options de JavaFX.
6. Ejecutar la clase:

```text
app.Main
```

Desde la pantalla principal se selecciona el caso que se desea visualizar.

---

## Formato general de los archivos JSON

Los archivos JSON deben seguir la siguiente estructura:

```json
{
  "ciudad": {
    "vertices": [
      { "id": "V01", "tipo": "DEPOT", "x": 80, "y": 80 }
    ],
    "aristas": [
      { "u": "V01", "v": "V02", "distancia": 100 }
    ]
  },
  "paquetes": [
    { "id": "P01", "destino": "V03", "peso": 8, "prioridad": 1 }
  ],
  "camiones": [
    { "id": "C01", "capacidad": 25 }
  ]
}
```

### Reglas del JSON

* Debe existir un único vértice de tipo `DEPOT`.
* Cada paquete debe tener un destino existente en el grafo.
* Las distancias de las aristas deben ser positivas.
* Los pesos de los paquetes deben ser positivos.
* Las prioridades deben estar entre 1 y 3.
* Las capacidades de los camiones deben ser positivas.

---

## Funcionalidades y complejidades principales

| Funcionalidad / Método             | Propósito                                                | Complejidad Temporal | Complejidad Espacial |
| ---------------------------------- | -------------------------------------------------------- | :------------------: | :------------------: |
| Carga JSON (JsonLoader)            | Leer vértices, aristas, paquetes y camiones              |   O(V + E + P + C)   |   O(V + E + P + C)   |
| Validación de destinos             | Verificar que los paquetes apunten a vértices existentes |       O(P × V)       |         O(1)         |
| Consulta visual Dijkstra           | Obtener camino mínimo entre origen y destino             |    O(V² + E log V)   |         O(V)         |
| Dibujo del grafo                   | Renderizar vértices, aristas, rutas y MST                |     O(V + E + R)     |    O(1) adicional    |
| Comparación de rutas               | Comparar Nearest Neighbor y MST-Based por camión         |   O(C × S²) aprox.   |       O(C + S)       |
| Detección de paquetes no asignados | Revisar asignación y motivo de rechazo                   |       O(P × C)       |    O(1) adicional    |

**Notación:**

* V = cantidad de vértices del grafo.
* E = cantidad de aristas del grafo.
* P = cantidad de paquetes.
* C = cantidad de camiones.
* S = cantidad de paradas únicas por camión.
* R = cantidad de rutas dibujadas.

---

## Observaciones

* La interfaz gráfica se desarrolló con JavaFX.
* La carga de JSON se realizó con Gson.
* Se agregaron varios casos de prueba para facilitar la validación del proyecto.
* El sistema permite cargar casos externos sin modificar el código.
* El panel completo permite revisar la información detallada del caso.
* El panel lateral principal se dejó como leyenda para mantener la visualización limpia.
* Se puede volver a la pantalla inicial para probar otro caso sin cerrar el programa.
* La consulta visual de Dijkstra refuerza el requisito de camino mínimo.
* El caso inaccesible permite demostrar la validación de conectividad con Warshall.
