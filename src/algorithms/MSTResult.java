package algorithms;

import graph.Edge;

/**
 * Clase MSTResult.
 * 
 * ""Esta clase no representa un elemento del grafo como Vertex o Edge""
 * 
 * Esta clase guarda el resultado producido por un algoritmo de MST como Prim o Kruskal.
 *
 * MST significa Minimum Spanning Tree o Árbol de Expansión Mínima.
 *
 * En LogísTEC, el MST representa el conjunto de calles de menor costo
 * que permite mantener conectados los puntos del grafo, siempre que el
 * grafo sea conexo (tipo de grafo en el que existe un camino directo o indirecto entre cualquier par de vértices (nodos).)
 *
 * Esta clase NO calcula el MST.
 * Solamente guarda:
 * - Las aristas que forman el MST.
 * - La cantidad de aristas agregadas.
 * - El costo total del MST.
 * - El tiempo de ejecución del algoritmo.
 *
 * Se crea esta clase para que el resultado pueda ser usado tanto en consola
 * como en la interfaz gráfica del integrante 4.
 */

public class MSTResult {

    // Nombre del algoritmo que generó este resultado: "Prim" o "Kruskal".
    private String algorithmName;

    // Arreglo que guarda las aristas seleccionadas para formar el MST.
    private Edge[] mstEdges;

    // Cantidad real de aristas que se han agregado al MST.
    private int edgeCount;

    // Suma total de los pesos de todas las aristas del MST.
    private int totalCost;

    // Tiempo que tardó el algoritmo en ejecutarse, medido en nanosegundos.
    private long executionTime;

    /**
     * Constructor de MSTResult.
     *
     * @param algorithmName nombre del algoritmo que genera el resultado.
     * @param maxEdges cantidad máxima de aristas que podría tener el MST.
     */
    public MSTResult(String algorithmName, int maxEdges) {
        this.algorithmName = algorithmName;
        this.mstEdges = new Edge[maxEdges];
        this.edgeCount = 0;
        this.totalCost = 0;
        this.executionTime = 0;
    }

    /**
     * Agrega una arista al resultado del MST.
     *
     * Cada vez que Prim o Kruskal seleccionen una arista válida,
     * llamarán a este método para guardarla.
     *
     * También se suma automáticamente el peso de la arista al costo total.
     *
     * @param edge arista que se desea agregar al MST.
     * @return true si la arista se agregó correctamente, false si no hay espacio.
     */
    public boolean addEdge(Edge edge) {

        // Si ya no hay espacio en el arreglo, no se puede agregar más.
        if (edgeCount >= mstEdges.length) {
            return false;
        }

        // Si sí
        // Guardamos la arista en la siguiente posición disponible.
        mstEdges[edgeCount] = edge;

        // Sumamos el peso de la arista al costo total del MST.
        totalCost += edge.getWeight();

        // Aumentamos la cantidad de aristas guardadas.
        edgeCount++;

        return true;
    }

    /**
     * Retorna el nombre del algoritmo usado.
     *
     * @return nombre del algoritmo.
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Retorna el arreglo de aristas del MST.
     *
     * Importante:
     * El arreglo puede tener posiciones null al final.
     * Por eso, al recorrerlo se debe usar edgeCount.
     *
     * @return arreglo de aristas del MST.
     */
    public Edge[] getMstEdges() {
        return mstEdges;
    }

    /**
     * Retorna la cantidad real de aristas agregadas al MST.
     *
     * @return cantidad de aristas del MST.
     */
    public int getEdgeCount() {
        return edgeCount;
    }

    /**
     * Retorna el costo total del MST.
     *
     * @return suma de los pesos de las aristas.
     */
    public int getTotalCost() {
        return totalCost;
    }

    /**
     * Guarda el tiempo de ejecución del algoritmo.
     *
     * Este valor se asigna después de que Prim o Kruskal terminan.
     *
     * @param executionTime tiempo en nanosegundos.
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Retorna el tiempo de ejecución del algoritmo.
     *
     * @return tiempo en nanosegundos.
     */
    public long getExecutionTime() { //Long para número entero grande, para ns 
        return executionTime;
    }

    /**
     * Verifica si el MST está completo.
     *
     * En un grafo conexo con V vértices, un MST válido debe tener V - 1 aristas.
     *
     * @param vertexCount cantidad de vértices del grafo original.
     * @return true si tiene exactamente vertexCount - 1 aristas.
     */
    public boolean isComplete(int vertexCount) {
        if (edgeCount == vertexCount - 1) {
            return true;
        }
        return false;
    }

    /**PRUEBAS
     * Imprime el resultado del MST en consola.
     *
     * Este método sirve para pruebas en Main.
     * Más adelante, la interfaz puede usar los getters en lugar de imprimir.
     */
    public void printResult() {
        System.out.println("\n--- RESULTADO MST: " + algorithmName + " ---");

        System.out.println("Aristas seleccionadas:");

        for (int i = 0; i < edgeCount; i++) {
            System.out.println(mstEdges[i]);
        }

        System.out.println("Costo total: " + totalCost + "m");
        System.out.println("Tiempo de ejecución: " + executionTime + " ns");
    }
}