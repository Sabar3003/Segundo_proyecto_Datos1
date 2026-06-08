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
