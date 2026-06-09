package model;

// Importación explícita de tu lista enlazada personalizada
import structures.MyLinkedList;

/**
 * Clase ShortestPathResult
 * Modela el resultado de un cálculo de ruta óptima en el grafo.
 * Garantiza compatibilidad absoluta con MyLinkedList, ReportGenerator, GraphPane e InfoPanel.
 */
public class ShortestPathResult {
    private String origin;
    private String destination;
    private int totalDistance;
    private boolean reachable;

    // Almacenamiento interno unificado como arreglo para facilitar el pintado en UI
    private String[] pathArray;
    private int vertexCount;

    /**
     * CONSTRUCTOR 1: Inicialización por tamaño máximo (Algoritmos con mallas o arreglos fijos)
     */
    public ShortestPathResult(String origin, String destination, int maxVertices) {
        this.origin = origin;
        this.destination = destination;
        this.totalDistance = Integer.MAX_VALUE;
        this.reachable = false;
        this.pathArray = new String[maxVertices > 0 ? maxVertices : 100];
        this.vertexCount = 0;
    }

    /**
     * CONSTRUCTOR 2: Adaptador directo para estructuras de tipo MyLinkedList<String>
     * Resuelve el error de tipos incompatibles en GraphAlgorithms.java (Línea 526)
     */
    public ShortestPathResult(String origin, String destination, int totalDistance, MyLinkedList<String> finalPath, int vertexCounter) {
        this.origin = origin;
        this.destination = destination;
        this.totalDistance = totalDistance;
        this.reachable = (totalDistance != Integer.MAX_VALUE && finalPath != null && vertexCounter > 0);
        this.vertexCount = vertexCounter;

        // Pasamos los elementos de MyLinkedList al arreglo interno secuencialmente
        if (finalPath != null && vertexCounter > 0) {
            this.pathArray = new String[vertexCounter];
            for (int i = 0; i < vertexCounter; i++) {
                // Se asume que MyLinkedList cuenta con un método .get(index) estándar
                this.pathArray[i] = finalPath.get(i);
            }
        } else {
            this.pathArray = new String[0];
            this.vertexCount = 0;
        }
    }

    // ==========================================
    // MÉTODOS DE LLENADO DINÁMICO
    // ==========================================

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void addPathVertex(String vertexId) {
        if (pathArray == null || pathArray.length == 0) {
            pathArray = new String[100];
        }
        if (vertexCount < pathArray.length) {
            pathArray[vertexCount] = vertexId;
            vertexCount++;
        }
    }

    // ==========================================
    // INTERFAZ DE COMPATIBILIDAD CON LA UI Y REPORTES
    // ==========================================

    /**
     * Verifica si la ruta es accesible (Línea 104 de GraphPane)
     */
    public boolean isReachable() {
        return this.reachable;
    }

    /**
     * Retorna la cantidad de vértices procesados en la ruta (Línea 118 de GraphPane)
     */
    public int getPathCount() {
        return this.vertexCount;
    }

    /**
     * Alias de conteo para ReportGenerator (Línea 370)
     */
    public int getVertexCount() {
        return this.vertexCount;
    }

    /**
     * Retorna el arreglo nativo purgado que GraphPane necesita para dibujar (Línea 108 de GraphPane)
     */
    public String[] getPath() {
        if (!reachable || vertexCount == 0 || pathArray == null) {
            return new String[0];
        }
        // Si el tamaño coincide de forma exacta, lo retorna directamente; si no, lo recorta
        if (pathArray.length == vertexCount) {
            return pathArray;
        }
        String[] exactPath = new String[vertexCount];
        System.arraycopy(pathArray, 0, exactPath, 0, vertexCount);
        return exactPath;
    }

    /**
     * Genera la hilera formateada de la ruta para el cuadro visual (Línea 184 de InfoPanel)
     */
    public String getPathAsText() {
        if (!reachable || vertexCount == 0 || pathArray == null) {
            return "No existe camino entre " + origin + " y " + destination + ".";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertexCount; i++) {
            if (pathArray[i] != null) {
                sb.append(pathArray[i]);
                if (i < vertexCount - 1) {
                    sb.append(" -> ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Variante de formato de texto para ReportGenerator
     */
    public String getFormattedPath() {
        return getPathAsText();
    }

    // ==========================================
    // GETTERS BÁSICOS
    // ==========================================

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getTotalDistance() { return totalDistance; }
}