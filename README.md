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
