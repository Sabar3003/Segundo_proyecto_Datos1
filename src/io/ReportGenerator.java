package io;

import algorithms.GraphAlgorithms;
import algorithms.MSTAlgorithms;
import algorithms.MSTResult;
import graph.Graph;
import model.AssignmentResult;
import model.Package;
import model.RouteResult;
import model.Truck;
import planner.PackageAssigner;
import planner.RoutePlanner;

/**
 * Clase ReportGenerator.
 *
 * Esta clase se encarga de generar un reporte final en consola.
 *
 * Su función principal es integrar las partes ya implementadas por el grupo:
 * - Validación de conectividad con Warshall.
 * - Matriz de distancias mínimas con Floyd-Warshall.
 * - Comparación de MST con Prim y Kruskal.
 * - Asignación de paquetes a camiones.
 * - Planificación de rutas con Nearest Neighbor y MST-Based.
 *
 * Esta clase no implementa algoritmos nuevos.
 * Solo usa las clases ya existentes para producir una salida clara y ordenada.
 */
public class ReportGenerator {

    /**
     * Genera el reporte completo del caso cargado desde JSON.
     *
     * @param data datos cargados desde JsonLoader.
     */
    public static void generateFullReport(LogisticsData data) {

        /*
         * Extraemos los datos principales.
         * Esto hace que el resto del código sea más fácil de leer.
         */
        Graph graph = data.getGraph();
        String depotId = data.getDepotId();
        Package[] packages = data.getPackages();
        Truck[] trucks = data.getTrucks();

        printTitle("REPORTE FINAL LOGISTEC");

        /*
         * Mostramos un resumen inicial del caso de prueba.
         */
        printGeneralSummary(graph, depotId, packages, trucks);

        /*
         * Validamos alcanzabilidad desde el depósito.
         * Warshall retorna una matriz booleana indicando si un vértice puede
         * alcanzar a otro.
         */
        printTitle("VALIDACION DE CONECTIVIDAD");
        boolean[][] reachabilityMatrix = GraphAlgorithms.warshall(graph, depotId);

        /*
         * Calculamos la matriz de distancias mínimas con Floyd-Warshall.
         * Esta matriz será usada después por las heurísticas de rutas.
         */
        printTitle("MATRIZ DE DISTANCIAS MINIMAS");
        int[][] distanceMatrix = GraphAlgorithms.getFloydWarshallDistanceMatrix(graph);
        System.out.println("Matriz de Floyd-Warshall calculada correctamente.");

        /*
         * Ejecutamos Prim y Kruskal para comparar el MST.
         */
        printTitle("COMPARACION DE MST");
        MSTResult primResult = MSTAlgorithms.prim(graph, depotId);
        MSTResult kruskalResult = MSTAlgorithms.kruskal(graph);

        printMSTComparison(primResult, kruskalResult);

        /*
         * Antes de asignar paquetes, filtramos los paquetes que realmente son
         * alcanzables desde el depósito.
         *
         * Los paquetes con destino inalcanzable no deberían asignarse a camiones.
         */
        Package[] reachablePackages = filterReachablePackages(
                graph,
                packages,
                depotId,
                reachabilityMatrix
        );

        /*
         * Ejecutamos la asignación de paquetes a camiones.
         * La clase PackageAssigner ya ordena por prioridad y peso.
         */
        printTitle("ASIGNACION DE PAQUETES");
        AssignmentResult assignmentResult =
                PackageAssigner.assignPackages(reachablePackages, trucks);

        assignmentResult.printReport();

        /*
         * Reportamos también los paquetes inalcanzables.
         * Estos se rechazan por conectividad, no por capacidad del camión.
         */
        printUnreachablePackages(graph, packages, depotId, reachabilityMatrix);

        /*
         * Para cada camión, calculamos y mostramos sus rutas.
         */
        printTitle("PLANIFICACION DE RUTAS");
        printTruckRoutes(graph, distanceMatrix, trucks, depotId);

        printTitle("FIN DEL REPORTE");
    }

    /**
     * Imprime un título visual para separar secciones del reporte.
     *
     * @param title título de la sección.
     */
    private static void printTitle(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("   " + title);
        System.out.println("==================================================");
    }

    /**
     * Imprime un resumen general del caso cargado.
     *
     * @param graph grafo de la ciudad.
     * @param depotId id del depósito.
     * @param packages paquetes cargados.
     * @param trucks camiones cargados.
     */
    private static void printGeneralSummary(
            Graph graph,
            String depotId,
            Package[] packages,
            Truck[] trucks
    ) {
        System.out.println("Deposito: " + depotId);
        System.out.println("Cantidad de vertices: " + graph.getVertexCount());
        System.out.println("Cantidad de paquetes: " + packages.length);
        System.out.println("Cantidad de camiones: " + trucks.length);
    }

    /**
     * Imprime una comparación resumida entre Prim y Kruskal.
     *
     * @param primResult resultado generado por Prim.
     * @param kruskalResult resultado generado por Kruskal.
     */
    private static void printMSTComparison(
            MSTResult primResult,
            MSTResult kruskalResult
    ) {
        /*
         * Imprimimos los resultados detallados usando el método que ya trae MSTResult.
         */
        primResult.printResult();
        kruskalResult.printResult();

        System.out.println();
        System.out.println("--- Verificacion de costos ---");
        System.out.println("Costo Prim: " + primResult.getTotalCost() + "m");
        System.out.println("Costo Kruskal: " + kruskalResult.getTotalCost() + "m");

        if (primResult.getTotalCost() == kruskalResult.getTotalCost()) {
            System.out.println("Resultado: ambos algoritmos obtuvieron el mismo costo total.");
        } else {
            System.out.println("Resultado: los costos son diferentes. Se recomienda revisar el MST.");
        }

        System.out.println();
        System.out.println("--- Tiempos empiricos ---");
        System.out.println("Tiempo Prim: " + primResult.getExecutionTime() + " ns");
        System.out.println("Tiempo Kruskal: " + kruskalResult.getExecutionTime() + " ns");
    }

    /**
     * Filtra los paquetes que sí son alcanzables desde el depósito.
     *
     * Esta validación es importante porque el proyecto indica que los paquetes
     * con destino inalcanzable no se deben asignar a ningún camión.
     *
     * @param graph grafo de la ciudad.
     * @param packages todos los paquetes cargados.
     * @param depotId id del depósito.
     * @param reachabilityMatrix matriz generada por Warshall.
     * @return arreglo con solo los paquetes alcanzables.
     */
    private static Package[] filterReachablePackages(
            Graph graph,
            Package[] packages,
            String depotId,
            boolean[][] reachabilityMatrix
    ) {
        int reachableCount = 0;

        /*
         * Primero contamos cuántos paquetes son alcanzables.
         * Esto se hace porque usamos arreglos normales y necesitamos saber
         * el tamaño antes de crear el nuevo arreglo.
         */
        for (int i = 0; i < packages.length; i++) {
            if (isPackageReachable(graph, packages[i], depotId, reachabilityMatrix)) {
                reachableCount++;
            }
        }

        /*
         * Creamos un arreglo exacto para los paquetes alcanzables.
         */
        Package[] reachablePackages = new Package[reachableCount];
        int index = 0;

        for (int i = 0; i < packages.length; i++) {
            if (isPackageReachable(graph, packages[i], depotId, reachabilityMatrix)) {
                reachablePackages[index] = packages[i];
                index++;
            }
        }

        return reachablePackages;
    }

    /**
     * Verifica si el destino de un paquete es alcanzable desde el depósito.
     *
     * @param graph grafo de la ciudad.
     * @param packageToCheck paquete que se desea validar.
     * @param depotId id del depósito.
     * @param reachabilityMatrix matriz de Warshall.
     * @return true si el destino del paquete es alcanzable.
     */
    private static boolean isPackageReachable(
            Graph graph,
            Package packageToCheck,
            String depotId,
            boolean[][] reachabilityMatrix
    ) {
        int depotIndex = graph.getVertexIndex(depotId);
        int destinationIndex = graph.getVertexIndex(packageToCheck.getDestinationVertexId());

        if (depotIndex == -1 || destinationIndex == -1) {
            return false;
        }

        return reachabilityMatrix[depotIndex][destinationIndex];
    }

    /**
     * Imprime los paquetes que no se pueden entregar porque su destino
     * no es alcanzable desde el depósito.
     *
     * @param graph grafo de la ciudad.
     * @param packages paquetes cargados.
     * @param depotId id del depósito.
     * @param reachabilityMatrix matriz de Warshall.
     */
    private static void printUnreachablePackages(
            Graph graph,
            Package[] packages,
            String depotId,
            boolean[][] reachabilityMatrix
    ) {
        System.out.println();
        System.out.println("--- PAQUETES INALCANZABLES DESDE EL DEPOSITO ---");

        int unreachableCount = 0;

        for (int i = 0; i < packages.length; i++) {
            if (!isPackageReachable(graph, packages[i], depotId, reachabilityMatrix)) {
                System.out.println(packages[i]);
                unreachableCount++;
            }
        }

        if (unreachableCount == 0) {
            System.out.println("No hay paquetes inalcanzables.");
        }
    }

    /**
     * Imprime las rutas calculadas para cada camión.
     *
     * Para cada camión se calculan dos rutas:
     * - Nearest Neighbor.
     * - MST-Based.
     *
     * Luego se comparan sus distancias y se muestra cuál conviene usar.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param trucks camiones con paquetes ya asignados.
     * @param depotId id del depósito.
     */
    private static void printTruckRoutes(
            Graph graph,
            int[][] distanceMatrix,
            Truck[] trucks,
            String depotId
    ) {
        for (int i = 0; i < trucks.length; i++) {
            Truck truck = trucks[i];

            System.out.println();
            System.out.println("--------------------------------------------------");
            System.out.println("Camion " + truck.getId());
            System.out.println("--------------------------------------------------");

            System.out.println("Carga transportada: "
                    + truck.getCurrentLoad() + "kg / "
                    + truck.getMaxCapacity() + "kg");

            System.out.println("Ocupacion: "
                    + truck.getOccupancyPercentage() + "%");

            System.out.println();
            System.out.println("Paquetes asignados:");

            Package[] assignedPackages = truck.getAssignedPackages();

            if (truck.getAssignedPackageCount() == 0) {
                System.out.println("Este camion no tiene paquetes asignados.");
            } else {
                for (int j = 0; j < truck.getAssignedPackageCount(); j++) {
                    System.out.println(assignedPackages[j]);
                }
            }

            /*
             * Calculamos las dos heurísticas usando RoutePlanner.
             */
            RouteResult nearestResult =
                    RoutePlanner.nearestNeighbor(graph, distanceMatrix, truck, depotId);

            RouteResult mstResult =
                    RoutePlanner.mstBasedRoute(graph, distanceMatrix, truck, depotId);

            /*
             * Imprimimos ambas rutas.
             */
            nearestResult.printRoute();
            mstResult.printRoute();

            /*
             * Comparamos distancias para elegir la mejor ruta.
             */
            printBestRoute(nearestResult, mstResult);
        }
    }

    /**
     * Compara dos rutas y muestra cuál tiene menor distancia total.
     *
     * @param nearestResult resultado de Nearest Neighbor.
     * @param mstResult resultado de MST-Based.
     */
    private static void printBestRoute(
            RouteResult nearestResult,
            RouteResult mstResult
    ) {
        int nearestDistance = nearestResult.getTotalDistance();
        int mstDistance = mstResult.getTotalDistance();

        System.out.println();
        System.out.println("--- Comparacion de heuristicas ---");
        System.out.println("Distancia Nearest Neighbor: " + nearestDistance + "m");
        System.out.println("Distancia MST-Based: " + mstDistance + "m");

        double savingPercentage = 0.0;

        if (nearestDistance != 0 && nearestDistance != Integer.MAX_VALUE) {
            savingPercentage =
                    ((nearestDistance - mstDistance) * 100.0) / nearestDistance;
        }

        System.out.println("Ahorro MST-Based sobre Nearest Neighbor: "
                + savingPercentage + "%");

        if (nearestDistance < mstDistance) {
            System.out.println("Mejor heuristica: Nearest Neighbor");
        } else if (mstDistance < nearestDistance) {
            System.out.println("Mejor heuristica: MST-Based");
        } else {
            System.out.println("Resultado: ambas heuristicas tienen la misma distancia.");
        }
    }
}