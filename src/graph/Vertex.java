package graph;

/**
 * Representa un vértice del grafo.
 *
 * En el contexto de LogísTEC, un vértice puede ser:
 * - DEPOT: depósito principal.
 * - INTERSECCION: intersección normal de la ciudad.
 * - ENTREGA: punto donde se debe entregar un paquete.
 */
public class Vertex {

    // Identificador único del vértice, por ejemplo: A, B, C, V01.
    private String id;

    // Tipo del vértice: DEPOT, INTERSECCION o ENTREGA.
    private String type;

    // Coordenada X para dibujar el vértice en la interfaz gráfica.
    private int x;

    // Coordenada Y para dibujar el vértice en la interfaz gráfica.
    private int y;

    /**
     * Constructor del vértice.
     *
     * @param id identificador único.
     * @param type tipo del vértice.
     * @param x coordenada x.
     * @param y coordenada y.
     */
    public Vertex(String id, String type, int x, int y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    /**
     * Retorna el identificador del vértice.
     *
     * @return id del vértice.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el tipo del vértice.
     *
     * @return tipo del vértice.
     */
    public String getType() {
        return type;
    }

    /**
     * Indica si el vértice es el depósito.
     *
     * @return true si el tipo es DEPOT.
     */
    public boolean isDepot() {
        return type.equalsIgnoreCase("DEPOT");
    }

    /**
     * Retorna la coordenada x.
     *
     * @return coordenada x.
     */
    public int getX() {
        return x;
    }

    /**
     * Retorna la coordenada y.
     *
     * @return coordenada y.
     */
    public int getY() {
        return y;
    }

    /**
     * Representación textual del vértice.
     *
     * @return texto con id y tipo.
     */
    @Override
    public String toString() {
        return id + " (" + type + ")";
    }
}