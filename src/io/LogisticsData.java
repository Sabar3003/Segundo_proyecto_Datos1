package io;

import graph.Graph;
import model.Package;
import model.Truck;

/**
 * Clase LogisticsData.
 *
 * Esta clase funciona como un contenedor de datos.
 *
 * Su propósito es guardar juntos todos los objetos principales que se cargan
 * desde el archivo JSON:
 * - El grafo de la ciudad.
 * - El id del depósito.
 * - Los paquetes.
 * - Los camiones.
 *
 * Esta clase no calcula rutas, no asigna paquetes y no ejecuta algoritmos.
 * Solo almacena la información ya construida por JsonLoader.
 */
public class LogisticsData {

    // Grafo no dirigido y ponderado que representa la ciudad.
    private Graph graph;

    // Identificador del vértice que funciona como depósito.
    private String depotId;

    // Arreglo con todos los paquetes cargados desde el JSON.
    private Package[] packages;

    // Arreglo con todos los camiones cargados desde el JSON.
    private Truck[] trucks;

    /**
     * Constructor de LogisticsData.
     *
     * @param graph grafo de la ciudad.
     * @param depotId id del depósito.
     * @param packages paquetes cargados.
     * @param trucks camiones cargados.
     */
    public LogisticsData(Graph graph, String depotId, Package[] packages, Truck[] trucks) {
        this.graph = graph;
        this.depotId = depotId;
        this.packages = packages;
        this.trucks = trucks;
    }

    /**
     * Retorna el grafo cargado.
     *
     * @return grafo de la ciudad.
     */
    public Graph getGraph() {
        return graph;
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
     * Retorna los paquetes cargados.
     *
     * @return arreglo de paquetes.
     */
    public Package[] getPackages() {
        return packages;
    }

    /**
     * Retorna los camiones cargados.
     *
     * @return arreglo de camiones.
     */
    public Truck[] getTrucks() {
        return trucks;
    }
}