package graph;

/**
 * Representa una arista ponderada entre dos vértices.
 *
 * En LogísTEC, una arista representa una calle entre dos puntos
 * de la ciudad, y el peso representa la distancia en metros.
 */
public class Edge {

    // Vértice de origen.
    private String from;

    // Vértice de destino.
    private String to;

    // Peso de la arista, en este caso distancia en metros.
    private int weight;

    /**
     * Constructor de la arista.
     *
     * @param from vértice origen.
     * @param to vértice destino.
     * @param weight peso o distancia.
     */
    public Edge(String from, String to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Retorna el vértice de origen.
     *
     * @return id del vértice origen.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Retorna el vértice de destino.
     *
     * @return id del vértice destino.
     */
    public String getTo() {
        return to;
    }

    /**
     * Retorna el peso de la arista.
     *
     * @return peso de la arista.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Representación textual de la arista.
     *
     * @return texto de la arista.
     */
    @Override
    public String toString() {
        return from + " -- " + to + " (" + weight + "m)";
    }
}