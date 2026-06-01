package algorithms;

import graph.AdjacencyNode;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import structures.MyLinkedList;
import structures.Node;
import structures.UnionFind;
import structures.MyPriorityQueue;

/**
 * Clase MSTAlgorithms.
 *
 * Esta clase contiene los algoritmos relacionados con MST:
 * - Prim
 * - Kruskal
 *
 * En LogísTEC, el MST permite encontrar un conjunto mínimo de calles
 * que mantiene conectados los vértices del grafo, siempre que el grafo sea conexo.
 *
 * Esta clase trabaja con:
 * - Graph: para leer los vértices y vecinos del grafo.
 * - Edge: para representar las aristas seleccionadas.
 * - MyLinkedList y Node: porque los vecinos del grafo están guardados en listas enlazadas.
 * - UnionFind: se usa en Kruskal.
 * - MyPriorityQueue: se usa en Prim.
 */
public class MSTAlgorithms {

    /**
     * Obtiene todas las aristas del grafo sin repetirlas.
     *
     * Importante:
     * El grafo es no dirigido, por eso cada arista está guardada dos veces:
     *
     * A -> B
     * B -> A
     *
     * Para evitar duplicados, solo agregamos la arista cuando:
     *
     * índice del origen < índice del destino
     *
     * Ejemplo:
     * Si A está en índice 0 y B en índice 1, agregamos A-B.
     * Cuando luego aparezca B-A, no se agrega porque 1 no es menor que 0.
     *
     * Este método es útil para Kruskal, porque Kruskal necesita revisar
     * todas las aristas ordenadas por peso.
     *
     * @param graph grafo de la ciudad.
     * @return arreglo con todas las aristas únicas del grafo.
     */

    private static Edge[] getAllEdges(Graph graph) {

        // Primero contamos cuántas aristas únicas hay.
        // Esto se hace porque estamos usando arreglos normales, entonces necesitamos saber 
        // el tamaño antes de crear el arreglo.
        int totalEdges = countUndirectedEdges(graph);

        // Creamos el arreglo exacto donde se guardarán las aristas.
        Edge[] edges = new Edge[totalEdges];

        // Esta variable indica la siguiente posición libre dentro del arreglo.
        int edgeIndex = 0;

        // Recorremos todos los vértices reales del grafo.
        for (int i = 0; i < graph.getVertexCount(); i++) {

            // Obtenemos el vértice actual usando el método getVertex de Graph.
            Vertex currentVertex = graph.getVertex(i);

            // Si por alguna razón la posición está vacía, pasamos a la siguiente.
            if (currentVertex == null) {
                continue;
            }

            // Guardamos el id del vértice actual.
            String fromId = currentVertex.getId();

            // Pedimos al grafo la lista de vecinos del vértice actual.
            // Este método viene de Graph.java.
            MyLinkedList<AdjacencyNode> neighbors = graph.getNeighbors(fromId);

            // Si el vértice no tiene vecinos, no hay aristas que sacar desde aquí.
            if (neighbors == null) {
                continue;
            }

            // Obtenemos el primer nodo de la lista enlazada de vecinos.
            Node<AdjacencyNode> currentNode = neighbors.getHead();

            // Recorremos todos los vecinos del vértice actual.
            while (currentNode != null) {

                // Cada nodo de la lista guarda un AdjacencyNode.
                // Ese AdjacencyNode tiene el destino y el peso de la conexión.
                AdjacencyNode neighbor = currentNode.getData();

                String toId = neighbor.getDestination();
                int weight = neighbor.getWeight();

                // Buscamos el índice interno del vértice destino.
                // Esto nos permite comparar posiciones y evitar duplicados.
                int toIndex = graph.getVertexIndex(toId);

                // Solo agregamos la arista si el índice del origen es menor
                // que el índice del destino.
                //
                // Así evitamos guardar A-B y B-A como si fueran dos aristas distintas.
                if (i < toIndex) {
                    edges[edgeIndex] = new Edge(fromId, toId, weight);
                    edgeIndex++;
                }

                // Avanzamos al siguiente vecino de la lista enlazada.
                currentNode = currentNode.getNext();
            }
        }

        return edges; // Retornar arreglo con todas las aristas únicas del grafo.
    }

    /**
     * countUndirectedEdges = contar aristas del grafo no dirigido.
     * Cuenta cuántas aristas únicas tiene el grafo.
     *
     * Como el grafo es no dirigido, cada arista aparece dos veces en la lista
     * de adyacencia. Por eso se usa la misma regla: índice del origen < índice del destino
     *
     * @param graph grafo de la ciudad.
     * @return cantidad de aristas únicas.
     */
    private static int countUndirectedEdges(Graph graph) {

        // Contador de aristas únicas.
        int count = 0;

        // Recorremos todos los vértices reales del grafo.
        for (int i = 0; i < graph.getVertexCount(); i++) {

            // Obtenemos el vértice actual.
            Vertex currentVertex = graph.getVertex(i);

            // Si está vacío, lo ignoramos.
            if (currentVertex == null) {
                continue;
            }

            // Obtenemos el id del vértice actual.
            String fromId = currentVertex.getId();

            // Obtenemos sus vecinos desde Graph.
            MyLinkedList<AdjacencyNode> neighbors = graph.getNeighbors(fromId);

            // Si no tiene vecinos, pasamos al siguiente vértice.
            if (neighbors == null) {
                continue;
            }
            
            // Recorremos la lista enlazada de vecinos.
            Node<AdjacencyNode> currentNode = neighbors.getHead();

            while (currentNode != null) {

                // Sacamos la información del vecino.
                AdjacencyNode neighbor = currentNode.getData();

                // Buscamos el índice del destino.
                int toIndex = graph.getVertexIndex(neighbor.getDestination());

                // Contamos solo una dirección de la arista.
                // Usamos la regla: índice del origen < índice del destino para evitar duplicados.
                if (i < toIndex) {
                    count++;
                }
                // Avanzamos al siguiente nodo de la lista.
                currentNode = currentNode.getNext();
            }
        }

        return count; // Retornar la cantidad de aristas únicas del grafo.
    }

    /**
     * Ordena un arreglo de aristas de menor a mayor peso.
     *
     * Este método se usa para Kruskal, porque Kruskal debe revisar primero
     * las aristas más baratas del grafo.
     *
     * Se implementa con bubble sort para mantenerlo simple y fácil
     * de entender. No es el más eficiente, pero sirve 
     *
     * @param edges arreglo de aristas que se desea ordenar.
     */
    private static void sortEdgesByWeight(Edge[] edges) {

        // Recorremos el arreglo varias veces.
        // En cada pasada, la arista más pesada va quedando hacia el final.
        for (int i = 0; i < edges.length - 1; i++) {

            // Comparamos pares de aristas vecinas.
            // El "- 1 - i" evita revisar al final las aristas que ya quedaron ordenadas.
            for (int j = 0; j < edges.length - 1 - i; j++) {

                // Si la arista actual pesa más que la siguiente, están en mal orden.
                if (edges[j].getWeight() > edges[j + 1].getWeight()) {

                    // Guardamos temporalmente la arista actual.
                    Edge temp = edges[j];

                    // Movemos la arista más liviana hacia la izquierda.
                    edges[j] = edges[j + 1];

                    // Movemos la arista más pesada hacia la derecha.
                    edges[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Implementación del algoritmo de Kruskal para construir el MST del grafo.
     *
     * Kruskal funciona revisando las aristas de menor a mayor peso
     * decidiendo a su vez si cuando elige una arista, forma ciclo o no.
     * Si no forma ciclo, la arista se agrega al MST.
     *
     * Para eso se usa UnionFind:
     * - Si los dos vértices están en conjuntos diferentes, se puede agregar.
     * - Si ya están en el mismo conjunto, agregarla formaría un ciclo.
     *
     * @param graph grafo de la ciudad.
     * @return resultado del MST generado por Kruskal.
     */
    public static MSTResult kruskal(Graph graph) {

        // Guardamos el tiempo inicial antes de ejecutar el algoritmo.
        long startTime = System.nanoTime();

        // Obtenemos la cantidad real de vértices del grafo.
        int vertexCount = graph.getVertexCount();

        // Un MST completo debe tener V - 1 aristas.
        MSTResult result = new MSTResult("Kruskal", vertexCount - 1); // Creamos el resultado con el nombre del algoritmo y la cantidad máxima de aristas.

        // Obtenemos todas las aristas únicas del grafo.
        Edge[] edges = getAllEdges(graph);

        // Ordenamos las aristas de menor a mayor peso.
        sortEdgesByWeight(edges);

        // Creamos la estructura UnionFind.
        // Cada vértice inicia en su propio conjunto.
        UnionFind unionFind = new UnionFind(vertexCount);

        // Recorremos las aristas ya ordenadas.
        for (int i = 0; i < edges.length; i++) {

            // Tomamos la arista actual.
            Edge currentEdge = edges[i];

            // Obtenemos los índices internos de sus dos extremos.
            // UnionFind trabaja con números, no con ids tipo "A" o "B".
            int fromIndex = graph.getVertexIndex(currentEdge.getFrom());
            int toIndex = graph.getVertexIndex(currentEdge.getTo());

            // Si alguno no existe, ignoramos esta arista por seguridad.
            if (fromIndex == -1 || toIndex == -1) {
                continue;
            }

            /*
            * union(fromIndex, toIndex) intenta unir los conjuntos.
            *
            * Si retorna true:
            * Significa que estaban en conjuntos diferentes, entonces agregar
            * esta arista NO forma ciclo.
            *
            * Si retorna false:
            * Significa que ya estaban conectados dentro del MST parcial,
            * entonces agregar esta arista formaría un ciclo.
            */
            if (unionFind.union(fromIndex, toIndex)) {
                result.addEdge(currentEdge);
            }

            // Si ya tenemos V - 1 aristas, el MST está completo.
            if (result.isComplete(vertexCount)) {
                break;
            }
        }

        // Guardamos el tiempo final después de ejecutar el algoritmo.
        long endTime = System.nanoTime();

        // La diferencia entre final e inicial es el tiempo total del algoritmo.
        result.setExecutionTime(endTime - startTime);

        return result;
    }

    /**
     * Agrega a la cola de prioridad las aristas candidatas que salen de un vértice.
     *
     * Una arista candidata es una arista que conecta:
     * - Un vértice que ya está dentro del MST.
     * - Con un vértice que todavía no ha sido visitado.
     *
     * Este método se usa en Prim cada vez que se agrega un nuevo vértice al MST.
     *
     * @param graph grafo de la ciudad.
     * @param vertexId vértice desde donde se revisan las aristas.
     * @param visited arreglo que indica cuáles vértices ya están en el MST.
     * @param priorityQueue cola de prioridad donde se guardan las aristas candidatas.
     */
    private static void addCandidateEdges(
            Graph graph,
            String vertexId,
            boolean[] visited,
            MyPriorityQueue<Edge> priorityQueue
    ) {

        // Pedimos al grafo la lista de vecinos del vértice actual.
        MyLinkedList<AdjacencyNode> neighbors = graph.getNeighbors(vertexId);

        // Si no tiene vecinos, no hay aristas candidatas que agregar.
        if (neighbors == null) {
            return;
        }

        // Recorremos la lista enlazada de vecinos.
        Node<AdjacencyNode> currentNode = neighbors.getHead();

        while (currentNode != null) {

            // Sacamos la información del vecino.
            AdjacencyNode neighbor = currentNode.getData();

            String destinationId = neighbor.getDestination();
            int weight = neighbor.getWeight();

            // Buscamos el índice del vecino.
            int destinationIndex = graph.getVertexIndex(destinationId);

            // Si el vecino existe y todavía no está visitado,
            // la arista es candidata para entrar al MST.
            if (destinationIndex != -1 && !visited[destinationIndex]) {

                // Creamos una arista desde el vértice actual hacia el vecino.
                Edge edge = new Edge(vertexId, destinationId, weight);

                // Insertamos la arista en la cola de prioridad.
                // La prioridad es el peso, para que salga primero la más barata.
                priorityQueue.insert(edge, weight);
            }

            // Avanzamos al siguiente vecino.
            currentNode = currentNode.getNext();
        }
    }

    /**
     * Implementación del algoritmo de Prim para construir el MST del grafo.
     *
     * Prim empieza desde un vértice inicial y va haciendo crecer el MST.
     *
     * En cada paso:
     * - Marca un vértice como visitado.
     * - Agrega a una cola de prioridad las aristas que salen de ese vértice.
     * - Toma la arista más barata disponible.
     * - Si esa arista lleva a un vértice no visitado, la agrega al MST.
     *
     * A diferencia de Kruskal, Prim no ordena todas las aristas al inicio.
     * Prim usa una cola de prioridad para escoger siempre la arista vecina
     * de menor peso.
     *
     * @param graph grafo de la ciudad.
     * @param startId id del vértice desde donde empieza Prim.
     * @return resultado del MST generado por Prim.
     */
    public static MSTResult prim(Graph graph, String startId) {

        // Guardamos el tiempo inicial antes de ejecutar el algoritmo.
        long startTime = System.nanoTime();

        // Obtenemos la cantidad real de vértices del grafo.
        int vertexCount = graph.getVertexCount();

        // Creamos el resultado donde se guardarán las aristas seleccionadas por Prim.
        MSTResult result = new MSTResult("Prim", vertexCount - 1);

        // Buscamos el índice interno del vértice inicial.
        int startIndex = graph.getVertexIndex(startId);

        // Si el vértice inicial no existe, no se puede ejecutar Prim.
        if (startIndex == -1) {
            System.out.println("Error: el vértice inicial " + startId + " no existe.");
            return result;
        }

        // Arreglo para saber cuáles vértices ya fueron agregados al MST.
        boolean[] visited = new boolean[vertexCount];

        // Cola de prioridad para guardar aristas candidatas.
        // La prioridad será el peso de cada arista.

        // Usamos vertexCount * vertexCount como capacidad amplia para pruebas,
        // porque en grafos pequeños es suficiente
        MyPriorityQueue<Edge> priorityQueue = new MyPriorityQueue<>(vertexCount * vertexCount);

        // Marcamos el vértice inicial como visitado.
        visited[startIndex] = true;

        // Agregamos a la cola todas las aristas que salen del vértice inicial.
        addCandidateEdges(graph, startId, visited, priorityQueue);

        // Mientras haya aristas candidatas y el MST aún no esté completo.
        while (!priorityQueue.isEmpty() && !result.isComplete(vertexCount)) {
            //Los ! hacen que el true sea false
            
            // Sacamos la arista con menor peso.
            Edge currentEdge = priorityQueue.extractMin();

            // Buscamos los índices de los extremos de la arista.
            int fromIndex = graph.getVertexIndex(currentEdge.getFrom());
            int toIndex = graph.getVertexIndex(currentEdge.getTo());

            // Si alguno no existe, se ignora por seguridad.
            if (fromIndex == -1 || toIndex == -1) {
                continue;
            }

            /*
            * Como Prim agrega aristas desde vértices visitados hacia vértices no visitados,
            * debemos identificar cuál extremo todavía no está dentro del MST.
            *
            * Puede pasar que:
            * - from está visitado y to no.
            * - to está visitado y from no.
            * - ambos ya están visitados, entonces esa arista formaría ciclo y se descarta.
            */
            String nextVertexId = null;
            int nextVertexIndex = -1;

            if (visited[fromIndex] && !visited[toIndex]) {
                nextVertexId = currentEdge.getTo();
                nextVertexIndex = toIndex;
            } else if (visited[toIndex] && !visited[fromIndex]) {
                nextVertexId = currentEdge.getFrom();
                nextVertexIndex = fromIndex;
            } else {
                // Si ambos extremos ya están visitados, agregar esta arista formaría un ciclo.
                continue;
            }

            // Agregamos la arista seleccionada al resultado del MST.
            result.addEdge(currentEdge);

            // Marcamos el nuevo vértice como parte del MST.
            visited[nextVertexIndex] = true;

            // Agregamos a la cola las aristas que salen del nuevo vértice.
            addCandidateEdges(graph, nextVertexId, visited, priorityQueue);
        }

        // Guardamos el tiempo final.
        long endTime = System.nanoTime();

        // Calculamos y guardamos el tiempo total de ejecución.
        result.setExecutionTime(endTime - startTime);

        // Si no se logró completar el MST, el grafo probablemente no es conexo.
        if (!result.isComplete(vertexCount)) {
            System.out.println("Advertencia: el grafo no es conexo. No se pudo construir un MST completo con Prim.");
        }

        return result;
    }

    /**
     * Compara los resultados de Prim y Kruskal sobre el mismo grafo.
     * Es necesario según el enunciado
     *
     * Este método ejecuta ambos algoritmos, imprime sus resultados y verifica
     * si los dos producen el mismo costo total.
     *
     * Importante:
     * Prim y Kruskal pueden seleccionar aristas en diferente orden, e incluso
     * podrían producir MST distintos si existen aristas con pesos repetidos.
     *
     * Sin embargo, si ambos están correctos, el costo total del MST debe ser igual.
     *
     * @param graph grafo de la ciudad.
     * @param startId vértice inicial para ejecutar Prim.
     */
    public static void comparePrimAndKruskal(Graph graph, String startId) {

        System.out.println("\n=================================");
        System.out.println("   COMPARACIÓN PRIM VS KRUSKAL   ");
        System.out.println("=================================");

        // Ejecutamos Kruskal sobre el grafo completo.
        MSTResult kruskalResult = kruskal(graph);

        // Ejecutamos Prim desde el vértice inicial indicado.
        MSTResult primResult = prim(graph, startId);

        // Imprimimos el resultado detallado de Kruskal.
        kruskalResult.printResult();

        // Imprimimos el resultado detallado de Prim.
        primResult.printResult();

        System.out.println("\n--- VERIFICACIÓN DE COSTOS ---");

        System.out.println("Costo Kruskal: " + kruskalResult.getTotalCost() + "m");
        System.out.println("Costo Prim: " + primResult.getTotalCost() + "m");

        /*
        * Si los costos son iguales, ambos algoritmos produjeron un MST
        * con el mismo costo total.
        *
        * Esto es lo esperado en un grafo conectado.
        */
        if (kruskalResult.getTotalCost() == primResult.getTotalCost()) {
            System.out.println("Resultado: ambos algoritmos generan el mismo costo total.");
        } else {
            System.out.println("Resultado: los costos son diferentes. Se debe revisar la implementación.");
        }

        System.out.println("\n--- TIEMPOS DE EJECUCIÓN ---");
        System.out.println("Tiempo Kruskal: " + kruskalResult.getExecutionTime() + " ns");
        System.out.println("Tiempo Prim: " + primResult.getExecutionTime() + " ns");
    }

    /** PRUEBA
     * Sirve para verificar en Main que las aristas del grafo se están obteniendo
     * correctamente y sin duplicados.
     *
     * Más adelante, cuando Prim y Kruskal estén implementados, este método
     * puede quedarse como apoyo para depuración o eliminarse.
     *
     * @param graph grafo de la ciudad.
     */
    public static void printAllEdgesForTest(Graph graph) {

        // Obtenemos todas las aristas únicas del grafo.
        Edge[] edges = getAllEdges(graph);

        System.out.println("\n--- ARISTAS ÚNICAS DEL GRAFO ---");

        // Recorremos el arreglo e imprimimos cada arista.
        for (int i = 0; i < edges.length; i++) {
            System.out.println(edges[i]);
        }

        System.out.println("Total de aristas únicas: " + edges.length);
    }

    /** PRUEBA 2
     * Método temporal de prueba.
     *
     * Obtiene todas las aristas únicas del grafo, las ordena por peso
     * y las imprime en consola.
     *
     * Sirve para verificar que el arreglo queda listo para Kruskal.
     *
     * @param graph grafo de la ciudad.
     */
    public static void printSortedEdgesForTest(Graph graph) {

        // Obtenemos todas las aristas únicas del grafo.
        Edge[] edges = getAllEdges(graph);

        // Ordenamos las aristas de menor a mayor peso.
        sortEdgesByWeight(edges);

        System.out.println("\n--- ARISTAS ORDENADAS POR PESO ---");

        // Imprimimos el arreglo ya ordenado.
        for (int i = 0; i < edges.length; i++) {
            System.out.println(edges[i]);
        }
    }
}