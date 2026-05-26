package graph;

/**
 * Representa un vecino dentro de la lista de adyacencia.
 *
 * Por ejemplo, si A está conectado con B con peso 320,
 * entonces dentro de la lista de A se guarda:
 *
 * destination = B
 * weight = 320
 */
public class AdjacencyNode {

    // Identificador del vértice vecino.
    private String destination;

    // Peso de la conexión hacia ese vecino.
    private int weight;

    /**
     * Constructor del nodo de adyacencia.
     *
     * @param destination id del vértice vecino.
     * @param weight peso de la conexión.
     */
    public AdjacencyNode(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    /**
     * Retorna el vértice vecino.
     *
     * @return id del destino.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Retorna el peso de la conexión.
     *
     * @return peso de la arista.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Representación textual del vecino.
     *
     * @return texto del vecino.
     */
    @Override
    public String toString() {
        return destination + " (" + weight + "m)";
    }
}