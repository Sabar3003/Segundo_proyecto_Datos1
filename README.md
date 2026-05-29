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
