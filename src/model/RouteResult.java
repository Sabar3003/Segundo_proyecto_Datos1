package model;

/**
 * Clase RouteResult.
 *
 * Guarda el resultado de una ruta calculada para un camión.
 *
 * Esta clase no calcula rutas.
 * Solo almacena:
 * - El nombre de la heurística usada.
 * - El id del camión.
 * - Las paradas en orden.
 * - La cantidad real de paradas.
 * - La distancia total recorrida.
 *
 * Se crea para que el resultado pueda imprimirse en consola
 * y también pueda ser usado después por la interfaz gráfica.
 */
public class RouteResult {

    // Nombre de la heurística usada, por ejemplo: "Nearest Neighbor" o "MST-Based".
    private String heuristicName;

    // Identificador del camión al que pertenece esta ruta.
    private String truckId;

    // Depósito desde donde inicia y termina la ruta.
    private String depotId;

    // Arreglo con las paradas en orden.
    private String[] orderedStops;

    // Cantidad real de paradas guardadas.
    private int stopCount;

    // Distancia total de la ruta en metros.
    private int totalDistance;

    /**
     * Constructor de RouteResult.
     *
     * @param heuristicName nombre de la heurística usada.
     * @param truckId id del camión.
     * @param depotId id del depósito.
     * @param maxStops cantidad máxima de paradas.
     */
    public RouteResult(String heuristicName, String truckId, String depotId, int maxStops) {
        this.heuristicName = heuristicName;
        this.truckId = truckId;
        this.depotId = depotId;
        this.orderedStops = new String[maxStops];
        this.stopCount = 0;
        this.totalDistance = 0;
    }

    /**
     * Agrega una parada al orden de visita.
     *
     * @param stopId id del vértice que se visitará.
     * @return true si se agregó correctamente, false si no hay espacio.
     */
    public boolean addStop(String stopId) {
        if (stopCount >= orderedStops.length) {
            return false;
        }

        orderedStops[stopCount] = stopId;
        stopCount++;

        return true;
    }

    /**
     * Retorna el nombre de la heurística.
     *
     * @return nombre de la heurística.
     */
    public String getHeuristicName() {
        return heuristicName;
    }

    /**
     * Retorna el id del camión.
     *
     * @return id del camión.
     */
    public String getTruckId() {
        return truckId;
    }

    /**
     * Retorna el id del depósito.
     *
     * @return id del depósito.
     */
    public String getDepotId() {
        return depotId;
    }

    /**
     * Retorna el arreglo de paradas ordenadas.
     *
     * Importante:
     * El arreglo puede tener posiciones null al final.
     * Para recorrerlo correctamente se debe usar stopCount.
     *
     * @return arreglo de paradas.
     */
    public String[] getOrderedStops() {
        return orderedStops;
    }

    /**
     * Retorna cuántas paradas reales tiene la ruta.
     *
     * @return cantidad de paradas.
     */
    public int getStopCount() {
        return stopCount;
    }

    /**
     * Retorna la distancia total de la ruta.
     *
     * @return distancia total en metros.
     */
    public int getTotalDistance() {
        return totalDistance;
    }

    /**
     * Cambia la distancia total de la ruta.
     *
     * La distancia se calcula en RoutePlanner y luego se guarda aquí.
     *
     * @param totalDistance distancia total en metros.
     */
    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * Imprime la ruta en consola.
     *
     * La ruta se muestra como:
     * depósito -> parada1 -> parada2 -> depósito
     */
    public void printRoute() {
        System.out.println("\n--- RUTA " + heuristicName + " | Camión " + truckId + " ---");

        System.out.print("Orden: " + depotId);

        for (int i = 0; i < stopCount; i++) {
            System.out.print(" -> " + orderedStops[i]);
        }

        System.out.println(" -> " + depotId);
        System.out.println("Distancia total: " + totalDistance + "m");
    }
}