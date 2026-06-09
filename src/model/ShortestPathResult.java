package model;

import structures.MyLinkedList;

/**
 * Clase de transferencia de datos para encapsular el resultado de una consulta específica del algoritmo de Dijkstra
 * entre un origen y destino determinados.
 */
public class ShortestPathResult {
    private String origin;               // ID del nodo inicial de la consulta
    private String destination;          // ID del nodo final/meta de la consulta
    private int totalDistance;           // Distancia mínima acumulada en metros (o Integer.MAX_VALUE si es inalcanzable)
    private MyLinkedList<String> path;   // Estructura lineal propia que almacena la secuencia ordenada del camino
    private int vertexCount;             // Contador total de paradas o vértices involucrados en la ruta

    /**
     * Constructor completo para inicializar todos los atributos del resultado.
     */
    public ShortestPathResult(String origin, String destination, int totalDistance, MyLinkedList<String> path, int vertexCount) {
        this.origin = origin;
        this.destination = destination;
        this.totalDistance = totalDistance;
        this.path = path;
        this.vertexCount = vertexCount;
    }

    /**
     * Procesa la lista enlazada del camino y la transforma en un formato legible para el usuario (ej: "V01 -> V07 -> V24").
     **@return String formateado con la ruta o "Inalcanzable" si no existe conexión.
     */
    public String getFormattedPath() {
        // Validación de seguridad si el camino no se pudo construir (grafo desconectado)
        if (path == null || path.isEmpty()) return "Inalcanzable";
        StringBuilder sb = new StringBuilder();
        structures.Node<String> current = path.getHead();

        // Recorrido secuencial O(n) sobre la lista propia para concatenar las paradas
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" -> "); // Añade el separador visual si no es el último nodo
            }
            current = current.getNext();
        }
        return sb.toString();
    }

    // Getters estándar para la extracción de datos en la interfaz o reportes
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getTotalDistance() { return totalDistance; }
    public MyLinkedList<String> getPath() { return path; }
    public int getVertexCount() { return vertexCount; }
}