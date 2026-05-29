package algorithms;

import graph.Graph;
import graph.Vertex;
import graph.AdjacencyNode;
import structures.MyQueue;
import structures.MyPriorityQueue;
import structures.MyLinkedList;
import structures.Node;

/**
 * Clase GraphAlgorithms
 * Contiene la implementación de los algoritmos de recorridos y caminos de la ciudad
 */
public class GraphAlgorithms {

    /**
     * METODO AUXILIAR: findVertexIndex
     * Este metodo nos ayuda a traducir el ID del vertice (ej. "A")
     *a su posición numérica en esos arreglos (ej. 0).
     * @param graph El grafo donde vamos a buscar.
     * @param id    El texto del vértice que queremos buscar.
     * @return      La posición (índice) del vértice, o -1 si no existe.
     */
    private static int findVertexIndex(Graph graph, String id) {
        Vertex[] vertices = graph.getVertices();
        // Recorremos solo los vértices que realmente existen en el grafo
        for (int i = 0; i < graph.getVertexCount(); i++) {
            // Si el ID del vértice actual coincide con el que buscamos, retornamos su posición
            if (vertices[i].getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * IMPLEMENTACIÓN DE BFS (Búsqueda en Amplitud)
     * Recorre el grafo por capas. Primero visita los vecinos directos,
     * luego los vecinos de los vecinos, etc. Usa una Cola para mantener el orden.
     * @param graph   El grafo a recorrer.
     * @param startId ID del nodo desde donde empezamos
     */
    public static void bfs(Graph graph, String startId) {
        int totalVertices = graph.getVertexCount();
        if (totalVertices == 0) return; // Si el grafo está vacío, no hacemos nada

        // Arreglo para recordar por cuáles nodos ya pasamos y no enciclar el programa
        boolean[] visited = new boolean[graph.getMaxVertices()];
        MyQueue<String> queue = new MyQueue<>(); // La cola procesa los nodos en el orden en que los descubrimos

        // Buscamos dónde está el nodo inicial en nuestros arreglos
        int startIndex = findVertexIndex(graph, startId);
        if (startIndex == -1) {
            System.out.println("El vértice inicial " + startId + " no existe.");
            return;
        }
        // Marcamos el nodo inicial como visitado y lo metemos a la cola
        visited[startIndex] = true;
        queue.enqueue(startId);
        System.out.print("Recorrido BFS desde " + startId + ": ");

        // Mientras haya nodos en la cola por procesar...
        while (!queue.isEmpty()) {
            // Sacamos el primero de la fila y lo imprimimos
            String currentId = queue.dequeue();
            System.out.print(currentId + " ");
            // Pedimos al grafo la lista de vecinos directos de este nodo actual
            MyLinkedList<AdjacencyNode> neighbors = graph.getNeighbors(currentId);

            // Si tiene vecinos, los revisamos uno por uno
            if (neighbors != null) {
                Node<AdjacencyNode> currentNode = neighbors.getHead();
                while (currentNode != null) {
                    // Obtenemos los datos del vecino (su ID y a qué distancia está)
                    AdjacencyNode adjNode = currentNode.getData();
                    String neighborId = adjNode.getDestination();
                    // Buscamos qué posición numérica tiene este vecino
                    int neighborIndex = findVertexIndex(graph, neighborId);
                    // Si el vecino existe en el grafo y AÚN NO lo hemos visitado, lo marcamos enviado al final para procesarlo despues
                    if (neighborIndex != -1 && !visited[neighborIndex]) {
                        visited[neighborIndex] = true;
                        queue.enqueue(neighborId);
                    }
                    // Pasamos al siguiente vecino en la lista enlazada
                    currentNode = currentNode.getNext();
                }
            }
        }
        System.out.println();
    }

    /**
     * IMPLEMENTACIÓN DE DFS (Búsqueda en Profundidad)
     * Este es el wrapper, el cual solo prepara el arreglo de visitados y llama al metodo recursivo real que hace el trabajo profundo.
     * @param graph   El grafo a recorrer.
     * @param startId ID del nodo desde donde empezamos.
     */
    public static void dfs(Graph graph, String startId) {
        int totalVertices = graph.getVertexCount();
        if (totalVertices == 0) return;
        // Arreglo en blanco (false) para rastrear las visitas
        boolean[] visited = new boolean[graph.getMaxVertices()];
        System.out.print("Recorrido DFS desde " + startId + ": ");
        // Iniciamos la recursión
        dfsHelper(graph, startId, visited);
        System.out.println();
    }

    /**
     * METODO AUXILIAR RECURSIVO PARA DFS
     * Va lo más profundo posible por un camino antes de retroceder.
     * Al llamarse a sí mismo, usa la "pila" interna del sistema (Call Stack).
     */
    private static void dfsHelper(Graph graph, String currentId, boolean[] visited) {
        int currentIndex = findVertexIndex(graph, currentId);
        // Condición de parada: Si el nodo no existe o ya pasamos por aquí, nos devolvemos.
        if (currentIndex == -1 || visited[currentIndex]) {
            return;
        }
        // Marcamos como visitado y procesa el nodo actual
        visited[currentIndex] = true;
        System.out.print(currentId + " ");

        // Pedimos la lista de vecinos de este nodo
        MyLinkedList<AdjacencyNode> neighbors = graph.getNeighbors(currentId);
        // Si hay vecinos, exploramos cada uno a profundidad
        if (neighbors != null) {
            Node<AdjacencyNode> currentNode = neighbors.getHead();
            while (currentNode != null) {
                // Extraemos la información del vecino
                AdjacencyNode adjNode = currentNode.getData();
                String neighborId = adjNode.getDestination();
                int neighborIndex = findVertexIndex(graph, neighborId);

                // Si no hemos visitado a este vecino, saltamos hacia él recursivamente = Pausa este nodo y se mete de lleno al vecino
                if (neighborIndex != -1 && !visited[neighborIndex]) {
                    dfsHelper(graph, neighborId, visited);
                }
                // Cuando regresa de lo profundo del vecino anterior, pasa al siguiente
                currentNode = currentNode.getNext();
            }
        }
    }
    /**
     * IMPLEMENTACIÓN DE WARSHALL (Cierre Transitivo)
     * Construye una matriz booleana (P) que nos dice si es posible llegar de un nodo a otro, sin importar cuántos saltos haya que dar.
     * Además, verifica específicamente qué puntos son alcanzables desde el depósito.
     * @param graph   El grafo de la ciudad.
     * @param depotId El ID del nodo depósito (ej. "A").
     * @return        La matriz bidimensional de alcanzabilidad.
     */
    public static boolean[][] warshall(Graph graph, String depotId) {
        int n = graph.getVertexCount();
        Vertex[] vertices = graph.getVertices();
        // Crea la matriz P de tamaño n x n
        boolean[][] P = new boolean[n][n];
        // Inicializa la matriz con las conexiones directas (calles)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Un nodo siempre tiene conectividad garantizada hacia sí mismo
                if (i == j) {
                    P[i][j] = true;
                } else {
                    P[i][j] = (graph.getWeight(vertices[i].getId(), vertices[j].getId()) != -1); // Si no es -1, significa que hay calle directa.
                }
            }
        }

        // Triple bucle anidado clásico de Warshall.
        // La variable 'k' representa el "nodo intermedio" que evaluamos para ver si nos ayuda a conectar dos puntos que antes no se alcanzaban directamente.
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    P[i][j] = P[i][j] || (P[i][k] && P[k][j]); // ¿Puedo llegar de 'i' a 'j'? Sí, si ya podía (P[i][j]) y O si puedo llegar pasando por el nodo puente 'k'
                }
            }
        }

        // VALIDACIÓN ESPECIAL: Revisar conectividad desde el depósito
        // Traducimos el ID de texto del depósito a su posición numérica en nuestros arreglos
        int depotIndex = findVertexIndex(graph, depotId);
        System.out.println("\n--- VALIDACIÓN DE DESTINOS DESDE EL DEPÓSITO (" + depotId + ") ---");
        // Si el depósito especificado no existe en el mapa vial, cancelamos la validación
        if (depotIndex == -1) {
            System.out.println("Error: El depósito " + depotId + " no se encuentra en el grafo.");
            return P;
        }

        // Revisamos la fila correspondiente al depósito para ver a quiénes alcanza
        for (int j = 0; j < n; j++) {
            // Evitamos evaluar el depósito consigo mismo
            if (j != depotIndex) {
                String targetId = vertices[j].getId();
                String targetType = vertices[j].getType();
                // Si la celda en la matriz es 'true', los camiones pueden llegar exitosamente
                if (P[depotIndex][j]) {
                    System.out.println("[ALCANZABLE] " + targetType + " (" + targetId + ") es accesible.");
                } else {
                    // Si es falso, este paquete se debe rechazar más adelante en el proyecto
                    System.out.println("[ALERTA - INALCANZABLE] " + targetType + " (" + targetId + ") NO es accesible. Los paquetes a este destino deben descartarse.");
                }
            }
        }
        return P; // Retornamos la matriz por si se ocupa en otra parte
    }

    /**
     * IMPLEMENTACIÓN DEL ALGORITMO DE DIJKSTRA
     * Encuentra el camino más corto (en metros) desde un nodo inicial (Depósito) hacia todos los demás nodos alcanzables de la ciudad.
     * Utiliza MyPriorityQueue para extraer siempre el nodo con la menor distancia acumulada en cada paso.
     * @param graph   El grafo de la ciudad.
     * @param startId ID del nodo de origen (ej. depósito "A").
     */
    public static void dijkstra(Graph graph, String startId) {
        int maxV = graph.getMaxVertices();
        Vertex[] vertices = graph.getVertices();
        int[] distances = new int[maxV]; // Arreglo para guardar la distancia mínima descubierta hacia cada nodo
        String[] predecessors = new String[maxV]; // Arreglo para guardar el "padre" de cada nodo y así reconstruir la ruta al final
        boolean[] visited = new boolean[maxV]; // Arreglo de control para saber qué nodos ya tienen su ruta óptima definitiva

        // Inicialización de arreglos
        for (int i = 0; i < maxV; i++) {
            distances[i] = Integer.MAX_VALUE; // Simulamos el "infinito"
            predecessors[i] = null;
            visited[i] = false;
        }
        // Ubicamos el nodo de partida y le asignamos una distancia de 0 (estamos ahí)
        int startIndex = indexOf(vertices, maxV, startId);
        if (startIndex == -1) {
            System.out.println("Error: El vértice inicial " + startId + " no existe.");
            return;
        }
        distances[startIndex] = 0;

        // Preparación de la Cola de Prioridad
        // Capacidad amplia (maxV * maxV) porque un mismo nodo puede entrar a la cola varias veces si encontramos caminos cada vez más cortos.
        MyPriorityQueue<String> pq = new MyPriorityQueue<>(maxV * maxV);
        pq.insert(startId, 0);

        // Exploración y Relajación
        while (!pq.isEmpty()) {
            String u = pq.extractMin(); // Extraemos el nodo 'u' que tenga la distancia acumulada más pequeña
            int uIndex = indexOf(vertices, maxV, u);

            if (uIndex == -1 || visited[uIndex]) { // Si el nodo es inválido o ya calculamos su ruta óptima, lo ignoramos
                continue;
            }
            visited[uIndex] = true; // Marcamos este nodo como procesado de forma definitiva

            // Revisamos TODOS los vértices de la ciudad para ver quiénes son vecinos de 'u'
            for (int vIndex = 0; vIndex < maxV; vIndex++) {
                if (vertices[vIndex] != null && !visited[vIndex]) { // Solo evaluamos vértices que existan y que no hayan sido procesados aún
                    String v = vertices[vIndex].getId();
                    // ¿Existe una calle directa entre 'u' y 'v'?
                    if (graph.hasEdge(u, v)) {
                        int weight = graph.getWeight(u, v); // Obtenemos los metros de esa calle
                        int alternativeDist = distances[uIndex] + weight; // Calculamos cuánto nos costaría llegar a 'v' pasando por 'u'

                        // Si el nuevo camino es más corto que el que conocíamos...
                        if (alternativeDist < distances[vIndex]) {
                            distances[vIndex] = alternativeDist;
                            predecessors[vIndex] = u; // Guardamos que para llegar a 'v', lo mejor es venir desde 'u'
                            pq.insert(v, alternativeDist); // Ingresa 'v' en la cola con su nueva distancia óptima
                        }
                    }
                }
            }
        }

        // Impresión de Resultados
        System.out.println("\n--- RUTAS MÁS CORTAS (DIJKSTRA) ---");
        System.out.println("Origen: " + startId);
        for (int i = 0; i < maxV; i++) { // Recorremos todos los nodos para imprimir cómo llegar a cada uno
            if (vertices[i] == null) continue;

            String targetId = vertices[i].getId();
            if (targetId.equals(startId)) continue; // No imprimimos la ruta para ir del origen al origen
            int finalDistance = distances[i];
            if (finalDistance == Integer.MAX_VALUE) { // Si la distancia sigue siendo infinito, significa que la calle no llega hasta allá
                System.out.println("Destino " + targetId + ": Inalcanzable");
            } else {
                // Reconstruimos la secuencia de calles caminando hacia atrás usando los predecesores
                String path = targetId;
                int currentTrackIndex = i;
                // Mientras el nodo actual tenga un padre del cual provino...
                while (predecessors[currentTrackIndex] != null) {
                    String prevId = predecessors[currentTrackIndex];
                    path = prevId + " -> " + path; // Acumulamos el texto agregando el padre a la izquierda
                    currentTrackIndex = indexOf(vertices, maxV, prevId); // Retrocedemos al padre
                }
                // Imprimimos el formato final limpio
                System.out.println("Destino " + targetId + ": " + finalDistance + "m | Ruta: [" + path + "]");
            }
        }
    }
    /**
     * METODO AUXILIAR: Búsqueda segura de índices.
     * Este metodo busca la posición numérica de un vértice leyendo directamente el arreglo. Así mantenemos la encapsulación.
     * @param vertices Arreglo con todos los vértices de la ciudad.
     * @param maxV     Capacidad máxima del arreglo.
     * @param id       Identificador de texto a buscar (ej. "A").
     * @return         La posición (índice) en el arreglo, o -1 si no existe.
     */
    private static int indexOf(Vertex[] vertices, int maxV, String id) {
        for (int i = 0; i < maxV; i++) {
            // Verificamos que la posición no sea nula antes de preguntar su ID
            if (vertices[i] != null && vertices[i].getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * IMPLEMENTACIÓN DEL ALGORITMO DE FLOYD-WARSHALL
     * Calcula las distancias más cortas entre TODOS los pares de nodos del grafo.
     * Genera una matriz de distancias donde la fila es el origen y la columna el destino.
     * @param graph El grafo que representa la red vial de la ciudad.
     */
    public static void floydWarshall(Graph graph) {
        int maxV = graph.getMaxVertices();
        Vertex[] vertices = graph.getVertices();
        // Matriz para almacenar las distancias mínimas entre cualquier par de nodos (i, j)
        int[][] dist = new int[maxV][maxV];

        // Inicialización de la matriz base
        for (int i = 0; i < maxV; i++) {
            for (int j = 0; j < maxV; j++) {
                // Si alguna de las posiciones en el arreglo está vacía, la ignoramos
                if (vertices[i] == null || vertices[j] == null) {
                    dist[i][j] = Integer.MAX_VALUE;
                    continue;
                }
                String idOrigen = vertices[i].getId();
                String idDestino = vertices[j].getId();
                if (i == j) {
                    dist[i][j] = 0; // La distancia de un nodo hacia sí mismo siempre es 0 metros
                } else if (graph.hasEdge(idOrigen, idDestino)) {
                    dist[i][j] = graph.getWeight(idOrigen, idDestino); // Si existe una calle directa, guardamos su peso (distancia)
                } else {
                    dist[i][j] = Integer.MAX_VALUE; // Si no hay calle, iniciamos la distancia en "infinito"
                }
            }
        }

        // El Triple Ciclo Central, 'k' actúa como el nodo puente, verificamos si pasar por 'k' hace que el viaje sea más corto.
        for (int k = 0; k < maxV; k++) {
            if (vertices[k] == null) continue; // Saltamos nodos inexistentes
            for (int i = 0; i < maxV; i++) {
                if (vertices[i] == null) continue;
                for (int j = 0; j < maxV; j++) {
                    if (vertices[j] == null) continue;

                    // Evita sumar valores a "infinito" (desbordamiento de memoria)
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE) {
                        int caminoConEscala = dist[i][k] + dist[k][j]; // ¿Es más barato ir directo, o hacer escala en 'k'?
                        if (caminoConEscala < dist[i][j]) {
                            dist[i][j] = caminoConEscala; // Actualizamos a la nueva mejor distancia
                        }
                    }
                }
            }
        }

        // Impresión Tabulada de Resultados
        System.out.println("\n================");
        System.out.println("   MATRIZ DE DISTANCIAS MÁS CORTAS (FLOYD-WARSHALL)    ");
        System.out.println("==================");

        // Imprimir encabezado de las columnas
        System.out.print(String.format("%-8s", "Ori/Des"));
        for (int i = 0; i < maxV; i++) {
            if (vertices[i] != null) {
                System.out.print(String.format("%-8s", vertices[i].getId()));
            }
        }
        System.out.println();

        // Imprimir cada fila de la matriz
        for (int i = 0; i < maxV; i++) {
            if (vertices[i] == null) continue;
            System.out.print(String.format("%-8s", vertices[i].getId() + " |")); // ID del nodo origen al inicio de la fila

            for (int j = 0; j < maxV; j++) {
                if (vertices[j] == null) continue;
                if (dist[i][j] == Integer.MAX_VALUE) {
                    System.out.print(String.format("%-8s", "INF")); // INF representa inalcanzable
                } else {
                    System.out.print(String.format("%-8d", dist[i][j]));
                }
            }
            System.out.println(); // Salto a la siguiente fila
        }
    }
}