package model;

/**
 * Clase ShortestPathResult.
 *
 * Esta clase guarda el resultado de una consulta de camino mínimo.
 *
 * Guarda:
 * - El vértice origen.
 * - El vértice destino.
 * - El camino en orden.
 * - La cantidad real de vértices del camino.
 * - La distancia total.
 * - Si el destino es alcanzable o no.
 */
public class ShortestPathResult {

    // Id del vértice origen.
    private String originId;

    // Id del vértice destino.
    private String destinationId;

    // Arreglo con los vértices del camino en orden.
    private String[] path;

    // Cantidad real de vértices dentro del camino.
    private int pathCount;

    // Distancia total del camino mínimo.
    private int totalDistance;

    // Indica si el destino se pudo alcanzar desde el origen.
    private boolean reachable;

    /**
     * Constructor de ShortestPathResult.
     *
     * @param originId id del origen.
     * @param destinationId id del destino.
     * @param maxPathSize cantidad máxima posible de vértices en el camino.
     */
    public ShortestPathResult(String originId, String destinationId, int maxPathSize) {
        this.originId = originId;
        this.destinationId = destinationId;
        this.path = new String[maxPathSize];
        this.pathCount = 0;
        this.totalDistance = Integer.MAX_VALUE;
        this.reachable = false;
    }

    /**
     * Agrega un vértice al camino.
     *
     * @param vertexId id del vértice que se desea agregar.
     * @return true si se agregó correctamente.
     */
    public boolean addPathVertex(String vertexId) {
        if (pathCount >= path.length) {
            return false;
        }

        path[pathCount] = vertexId;
        pathCount++;

        return true;
    }

    /**
     * Retorna el origen del camino.
     *
     * @return id del origen.
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * Retorna el destino del camino.
     *
     * @return id del destino.
     */
    public String getDestinationId() {
        return destinationId;
    }

    /**
     * Retorna el arreglo del camino.
     *
     * Importante:
     * El arreglo puede tener posiciones null al final.
     * Para recorrerlo correctamente se debe usar pathCount.
     *
     * @return arreglo del camino.
     */
    public String[] getPath() {
        return path;
    }

    /**
     * Retorna cuántos vértices reales tiene el camino.
     *
     * @return cantidad de vértices en el camino.
     */
    public int getPathCount() {
        return pathCount;
    }

    /**
     * Retorna la distancia total.
     *
     * @return distancia total en metros.
     */
    public int getTotalDistance() {
        return totalDistance;
    }

    /**
     * Cambia la distancia total.
     *
     * @param totalDistance distancia calculada por Dijkstra.
     */
    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * Indica si el destino es alcanzable desde el origen.
     *
     * @return true si hay camino.
     */
    public boolean isReachable() {
        return reachable;
    }

    /**
     * Cambia el estado de alcanzabilidad.
     *
     * @param reachable true si el camino existe.
     */
    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    /**
     * Convierte el camino a texto.
     *
     * Ejemplo:
     * V01 -> V07 -> V13 -> V24
     *
     * @return camino en formato texto.
     */
    public String getPathAsText() {
        if (!reachable || pathCount == 0) {
            return "No existe camino.";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < pathCount; i++) {
            builder.append(path[i]);

            if (i < pathCount - 1) {
                builder.append(" -> ");
            }
        }

        return builder.toString();
    }
}