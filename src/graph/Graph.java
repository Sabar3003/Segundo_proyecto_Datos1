package graph;

import structures.MyLinkedList;
import structures.Node;

/**
 * Grafo no dirigido y ponderado implementado con lista de adyacencia.
 *
 * Este grafo representa la ciudad de LogísTEC.
 *
 * Características:
 * - No dirigido: si existe A-B, también existe B-A.
 * - Ponderado: cada arista tiene un peso o distancia.
 * - Lista de adyacencia: cada vértice tiene una lista de vecinos.
 */
public class Graph {

    // Arreglo de vértices del grafo.
    private Vertex[] vertices;

    // Arreglo donde cada posición contiene la lista de vecinos de un vértice.
    private MyLinkedList<AdjacencyNode>[] adjacencyList;

    // Cantidad actual de vértices agregados.
    private int vertexCount;

    /**
     * Constructor del grafo.
     *
     * @param maxVertices cantidad máxima de vértices que tendrá el grafo.
     */
    @SuppressWarnings("unchecked")
    public Graph(int maxVertices) {

        // Se crea el arreglo que guarda los vértices.
        vertices = new Vertex[maxVertices];

        // Se crea el arreglo de listas de adyacencia.
        adjacencyList = new MyLinkedList[maxVertices];

        // Al inicio no hay vértices agregados.
        vertexCount = 0;

        // Cada posición del arreglo necesita tener una lista vacía.
        for (int i = 0; i < maxVertices; i++) {
            adjacencyList[i] = new MyLinkedList<>();
        }
    }

    /**
     * Agrega un vértice al grafo.
     *
     * @param vertex vértice que se desea agregar.
     * @return true si se agregó correctamente, false si no se pudo.
     */
    public boolean addVertex(Vertex vertex) {

        // Si ya se llegó al máximo de vértices, no se puede agregar.
        if (vertexCount >= vertices.length) {
            return false;
        }

        // Evitamos agregar dos vértices con el mismo id.
        if (getVertexIndex(vertex.getId()) != -1) {
            return false;
        }

        // Guardamos el vértice en la siguiente posición disponible.
        vertices[vertexCount] = vertex;

        // Aumentamos el contador de vértices.
        vertexCount++;

        return true;
    }

    /**
     * Agrega una arista no dirigida entre dos vértices.
     *
     * Como el grafo es no dirigido, se agrega la conexión en ambos sentidos:
     * from -> to
     * to -> from
     *
     * @param from id del primer vértice.
     * @param to id del segundo vértice.
     * @param weight peso de la arista.
     * @return true si la arista se agregó correctamente.
     */
    public boolean addEdge(String from, String to, int weight) {

        // Buscamos la posición interna de ambos vértices.
        int fromIndex = getVertexIndex(from);
        int toIndex = getVertexIndex(to);

        // Si alguno no existe, no se puede crear la arista.
        if (fromIndex == -1 || toIndex == -1) {
            return false;
        }

        // El peso debe ser positivo.
        if (weight <= 0) {
            return false;
        }

        // Agregamos la conexión from -> to.
        adjacencyList[fromIndex].add(new AdjacencyNode(to, weight));

        // Agregamos la conexión to -> from porque es no dirigido.
        adjacencyList[toIndex].add(new AdjacencyNode(from, weight));

        return true;
    }

    /**
     * Busca el índice interno de un vértice usando su id.
     *
     * @param id identificador del vértice.
     * @return índice del vértice o -1 si no existe.
     */
    public int getVertexIndex(String id) {

        // Recorremos solo los vértices realmente agregados.
        for (int i = 0; i < vertexCount; i++) {

            // Comparamos el id del vértice actual con el id buscado.
            if (vertices[i].getId().equals(id)) {
                return i;
            }
        }

        // Si no se encontró, retornamos -1.
        return -1;
    }

    /**
     * Obtiene un vértice por índice.
     *
     * @param index posición del vértice.
     * @return vértice encontrado o null si el índice no es válido.
     */
    public Vertex getVertex(int index) {

        if (index < 0 || index >= vertexCount) {
            return null;
        }

        return vertices[index];
    }

    /**
     * Obtiene un vértice por su id.
     *
     * @param id identificador del vértice.
     * @return vértice encontrado o null si no existe.
     */
    public Vertex getVertexById(String id) {

        int index = getVertexIndex(id);

        if (index == -1) {
            return null;
        }

        return vertices[index];
    }

    /**
     * Obtiene los vecinos de un vértice.
     *
     * @param vertexId id del vértice.
     * @return lista de vecinos o null si el vértice no existe.
     */
    public MyLinkedList<AdjacencyNode> getNeighbors(String vertexId) {

        int index = getVertexIndex(vertexId);

        if (index == -1) {
            return null;
        }

        return adjacencyList[index];
    }

    /**
     * Obtiene el peso de una arista directa entre dos vértices.
     *
     * Importante:
     * Este método solo revisa si hay conexión directa.
     * No calcula caminos mínimos.
     *
     * @param from vértice origen.
     * @param to vértice destino.
     * @return peso de la arista o -1 si no existe conexión directa.
     */
    public int getWeight(String from, String to) {

        int fromIndex = getVertexIndex(from);

        // Si el vértice origen no existe, no hay conexión.
        if (fromIndex == -1) {
            return -1;
        }

        // Recorremos la lista de vecinos del vértice origen.
        Node<AdjacencyNode> current = adjacencyList[fromIndex].getHead();

        while (current != null) {

            AdjacencyNode neighbor = current.getData();

            // Si encontramos el vecino buscado, retornamos el peso.
            if (neighbor.getDestination().equals(to)) {
                return neighbor.getWeight();
            }

            current = current.getNext();
        }

        // Si no se encontró la conexión directa, retornamos -1.
        return -1;
    }

    /**
     * Retorna la cantidad de vértices agregados al grafo.
     *
     * @return cantidad actual de vértices.
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Imprime el grafo en consola.
     *
     * Este método sirve para verificar visualmente que las conexiones
     * se están guardando correctamente.
     */
    public void printGraph() {

        System.out.println("Grafo LogisTEC:");
        System.out.println("----------------");

        // Recorremos cada vértice agregado.
        for (int i = 0; i < vertexCount; i++) {

            // Imprimimos el vértice actual.
            System.out.print(vertices[i].getId() + " -> ");

            // Obtenemos el primer nodo de su lista de vecinos.
            Node<AdjacencyNode> current = adjacencyList[i].getHead();

            // Recorremos todos sus vecinos.
            while (current != null) {

                System.out.print(current.getData() + " ");

                current = current.getNext();
            }

            System.out.println();
        }
    }

    /**
     * Verifica si existe una arista directa entre dos vértices.
     *
     * @param from vértice origen.
     * @param to vértice destino.
     * @return true si existe conexión directa.
     */
    public boolean hasEdge(String from, String to) {
        return getWeight(from, to) != -1;
    }
    /**
     * Retorna la capacidad máxima del arreglo de vértices.
     *
     * @return capacidad máxima del grafo.
     */
    public int getMaxVertices() {
        return vertices.length;
    }
    /**
     * Retorna el arreglo interno de vértices.
     *
     * Este método permite que los algoritmos recorran los vértices.
     * Se debe tener cuidado de usar solo las posiciones desde 0 hasta vertexCount - 1.
     *
     * @return arreglo de vértices.
     */
    public Vertex[] getVertices() {
        return vertices;
    }
}