package model;

/**
 * Clase Package.
 *
 * Representa un paquete que debe ser entregado por LogísTEC.
 *
 * Según el proyecto, cada paquete tiene:
 * - Un identificador único.
 * - Un vértice de destino dentro del grafo.
 * - Un peso en kilogramos.
 * - Una prioridad entre 1 y 3, donde 1 es la prioridad más alta.
 *
 * Esta clase solo guarda información del paquete.
 * No decide a qué camión se asigna ni calcula rutas.
 * Esa responsabilidad es de la clase del paquete planner.
 */
public class Package {

    // Identificador único del paquete, por ejemplo: P01, P02, P03.
    private String id;

    // Id del vértice de destino donde debe entregarse el paquete.
    private String destinationVertexId;

    // Peso del paquete en kilogramos.
    private int weight;

    // Prioridad del paquete.
    // 1 = prioridad alta, 2 = prioridad media, 3 = prioridad baja.
    private int priority;

    /**
     * Constructor de Package.
     *
     * @param id identificador único del paquete.
     * @param destinationVertexId id del vértice de destino.
     * @param weight peso del paquete en kilogramos.
     * @param priority prioridad del paquete.
     */
    public Package(String id, String destinationVertexId, int weight, int priority) {
        this.id = id;
        this.destinationVertexId = destinationVertexId;
        this.weight = weight;
        this.priority = priority;
    }

    /**
     * Retorna el identificador del paquete.
     *
     * @return id del paquete.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el vértice destino del paquete.
     *
     * Este valor debe coincidir con el id de algún vértice del grafo.
     *
     * @return id del vértice destino.
     */
    public String getDestinationVertexId() {
        return destinationVertexId;
    }

    /**
     * Retorna el peso del paquete.
     *
     * @return peso en kilogramos.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Retorna la prioridad del paquete.
     *
     * @return prioridad del paquete.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Retorna una representación textual del paquete.
     *
     * Esto sirve para imprimir pruebas en consola.
     *
     * @return texto con la información principal del paquete.
     */
    @Override
    public String toString() {
        return id
                + " -> destino: " + destinationVertexId
                + ", peso: " + weight + "kg"
                + ", prioridad: " + priority;
    }
}