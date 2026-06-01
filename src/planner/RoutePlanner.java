package planner;

import graph.Graph;
import graph.Vertex;
import model.Package;
import model.RouteResult;
import model.Truck;
import graph.Edge;
import structures.UnionFind;

/**
 * Clase RoutePlanner.
 *
 * Esta clase se encarga de planificar rutas para los camiones.
 *
 * Según el proyecto, se deben implementar dos heurísticas:
 * - Nearest Neighbor.
 * - Heurística basada en MST.
 */
public class RoutePlanner {

    /**
     * Obtiene las paradas únicas de un camión.
     *
     * Un camión puede tener varios paquetes hacia el mismo destino.
     * Por ejemplo:
     * - P01 va a D.
     * - P02 también va a D.
     *
     * En ese caso, la ruta solo debe visitar D una vez.
     *
     * @param truck camión con paquetes asignados.
     * @return arreglo con los destinos únicos del camión.
     */
    public static String[] getUniqueStops(Truck truck) {

        // Como máximo, la cantidad de paradas únicas será igual a la cantidad de paquetes asignados.
        String[] uniqueStops = new String[truck.getAssignedPackageCount()];

        // Contador de paradas únicas encontradas.
        int uniqueCount = 0;

        // Obtenemos el arreglo de paquetes asignados al camión.
        Package[] packages = truck.getAssignedPackages();

        // Recorremos solo los paquetes reales usando getAssignedPackageCount().
        for (int i = 0; i < truck.getAssignedPackageCount(); i++) {

            // Sacamos el destino del paquete actual.
            String destinationId = packages[i].getDestinationVertexId();

            // Si ese destino todavía no está en uniqueStops, lo agregamos.
            if (!containsStop(uniqueStops, uniqueCount, destinationId)) {
                uniqueStops[uniqueCount] = destinationId;
                uniqueCount++;
            }
        }

        /*
         * Creamos un segundo arreglo con el tamaño exacto.
         *
         * Esto evita retornar un arreglo con null al final.
         * Por ejemplo:
         *
         * uniqueStops original:
         * [C, D, null, null]
         *
         * exactStops:
         * [C, D]
         */
        String[] exactStops = new String[uniqueCount];

        for (int i = 0; i < uniqueCount; i++) {
            exactStops[i] = uniqueStops[i];
        }

        return exactStops;
    }

    /**
     * Verifica si una parada ya está dentro de un arreglo.
     *
     * Este método ayuda a evitar destinos repetidos.
     *
     * @param stops arreglo de paradas.
     * @param stopCount cantidad real de paradas válidas dentro del arreglo.
     * @param stopId parada que se desea buscar.
     * @return true si la parada ya existe en el arreglo.
     */
    private static boolean containsStop(String[] stops, int stopCount, String stopId) {

        for (int i = 0; i < stopCount; i++) {
            if (stops[i].equals(stopId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Busca el índice de un vértice dentro del grafo.
     *
     * Este método simplemente llama a graph.getVertexIndex(id).
     * Se deja como auxiliar para que el código de rutas sea más fácil de leer.
     *
     * @param graph grafo de la ciudad.
     * @param vertexId id del vértice.
     * @return índice del vértice o -1 si no existe.
     */
    private static int getIndex(Graph graph, String vertexId) {
        return graph.getVertexIndex(vertexId);
    }

    /**
     * Obtiene la distancia mínima entre dos vértices usando una matriz de distancias.
     *
     * La matriz de distancias viene de ***Floyd-Warshall.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param fromId vértice origen.
     * @param toId vértice destino.
     * @return distancia mínima entre ambos vértices o Integer.MAX_VALUE si no existe.
     */
    private static int getDistance(
            Graph graph,
            int[][] distanceMatrix,
            String fromId,
            String toId
    ) {

        // Convertimos los ids de vértices a índices de la matriz.
        int fromIndex = getIndex(graph, fromId);
        int toIndex = getIndex(graph, toId);

        // Si alguno no existe, no se puede calcular distancia.
        if (fromIndex == -1 || toIndex == -1) {
            return Integer.MAX_VALUE;
        }

        // Retornamos la distancia guardada en la matriz.
        return distanceMatrix[fromIndex][toIndex];
    }

    /**
     * Calcula la distancia total de una ruta.
     *
     * La ruta se entiende así:
     *
     * depósito -> parada 1 -> parada 2 -> ... -> última parada -> depósito
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param depotId id del depósito.
     * @param stops paradas en orden.
     * @param stopCount cantidad real de paradas.
     * @return distancia total de la ruta.
     */
    private static int calculateRouteDistance(
            Graph graph,
            int[][] distanceMatrix,
            String depotId,
            String[] stops,
            int stopCount
    ) {

        // Si no hay paradas, la distancia es 0.
        if (stopCount == 0) {
            return 0;
        }

        int totalDistance = 0;

        // La ruta empieza en el depósito.
        String currentId = depotId;

        // Sumamos la distancia desde el punto actual hacia cada parada.
        for (int i = 0; i < stopCount; i++) {

            int distance = getDistance(graph, distanceMatrix, currentId, stops[i]);

            // Si alguna distancia es infinita, la ruta no es válida.
            if (distance == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            totalDistance += distance;

            // Ahora la posición actual pasa a ser la parada visitada.
            currentId = stops[i];
        }

        // Al final el camión debe volver al depósito.
        int returnDistance = getDistance(graph, distanceMatrix, currentId, depotId);

        if (returnDistance == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        totalDistance += returnDistance;

        return totalDistance;
    }

    /**
     * Planifica una ruta usando la heurística Nearest Neighbor.
     *
     * Nearest Neighbor funciona así:
     * 1. Empieza en el depósito.
     * 2. Busca la parada no visitada más cercana.
     * 3. Se mueve a esa parada.
     * 4. Repite hasta visitar todas las paradas.
     * 5. Finalmente regresa al depósito.
     *
     * Esta heurística es rápida y fácil de entender, pero no siempre da la ruta óptima.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas calculada con Floyd-Warshall.
     * @param truck camión al que se le calculará la ruta.
     * @param depotId id del depósito.
     * @return resultado de la ruta calculada.
     */
    public static RouteResult nearestNeighbor(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        // Obtenemos las paradas únicas del camión.
        // Si un camión tiene dos paquetes al mismo destino, solo visitamos ese destino una vez.
        String[] stops = getUniqueStops(truck);

        // Creamos el resultado de la ruta.
        RouteResult result = new RouteResult(
                "Nearest Neighbor",
                truck.getId(),
                depotId,
                stops.length
        );

        // Si el camión no tiene paradas, la ruta queda vacía y distancia 0.
        if (stops.length == 0) {
            result.setTotalDistance(0);
            return result;
        }

        // Arreglo para marcar cuáles paradas ya fueron visitadas.
        boolean[] visitedStops = new boolean[stops.length];

        // La ruta empieza desde el depósito.
        String currentId = depotId;

        // Cantidad de paradas visitadas hasta el momento.
        int visitedCount = 0;

        // Mientras todavía falten paradas por visitar.
        while (visitedCount < stops.length) {

            // Buscamos la parada no visitada más cercana desde currentId.
            int nearestIndex = findNearestUnvisitedStop(
                    graph,
                    distanceMatrix,
                    currentId,
                    stops,
                    visitedStops
            );

            // Si no se encontró una parada alcanzable, detenemos la ruta.
            if (nearestIndex == -1) { // El -1 indica que no hay ninguna parada disponible.
                break;
            }

            // Marcamos esa parada como visitada.
            visitedStops[nearestIndex] = true;

            // Agregamos la parada al resultado en el orden encontrado.
            result.addStop(stops[nearestIndex]);

            // Ahora el camión está ubicado en esa parada.
            currentId = stops[nearestIndex];

            // Aumentamos el contador de paradas visitadas.
            visitedCount++;
        }

        // Calculamos la distancia total:
        // depósito -> paradas en orden -> depósito.
        int totalDistance = calculateRouteDistance(
                graph,
                distanceMatrix,
                depotId,
                result.getOrderedStops(),
                result.getStopCount()
        );

        // Guardamos la distancia total en el resultado.
        result.setTotalDistance(totalDistance);

        return result;
    }

    /**
     * Busca la parada no visitada más cercana desde un vértice actual.
     *
     * Este método se usa dentro de Nearest Neighbor.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param currentId vértice donde está actualmente el camión.
     * @param stops arreglo de paradas del camión.
     * @param visitedStops arreglo que indica cuáles paradas ya fueron visitadas.
     * @return índice de la parada más cercana o -1 si no hay ninguna disponible.
     */
    private static int findNearestUnvisitedStop(
            Graph graph,
            int[][] distanceMatrix,
            String currentId,
            String[] stops,
            boolean[] visitedStops
    ) {

        // Índice de la mejor parada encontrada.
        int nearestIndex = -1;

        // Distancia más corta encontrada hasta el momento.
        int shortestDistance = Integer.MAX_VALUE;

        // Revisamos todas las paradas.
        for (int i = 0; i < stops.length; i++) {

            // Solo nos interesan las paradas que todavía no se han visitado.
            if (!visitedStops[i]) {

                // Calculamos la distancia desde la ubicación actual hasta esta parada.
                int distance = getDistance(graph, distanceMatrix, currentId, stops[i]);

                // Si la distancia es menor que la mejor encontrada,
                // esta parada pasa a ser la más cercana.
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestIndex = i;
                }
            }
        }

        return nearestIndex;
    }

    /**
     * Construye el arreglo de puntos que se usarán para la heurística MST-Based.
     *
     * La heurística MST-Based no trabaja con todo el grafo original.
     * Trabaja únicamente con:
     *
     * depósito + paradas únicas del camión
     *
     * Ejemplo:
     * depósito = A
     * paradas = [C, D]
     *
     * routePoints = [A, C, D]
     *
     * El depósito se coloca en la posición 0 porque el recorrido DFS preorden
     * debe iniciar desde el depósito.
     *
     * @param depotId id del depósito.
     * @param stops paradas únicas del camión.
     * @return arreglo con depósito y paradas.
     */
    private static String[] buildRoutePoints(String depotId, String[] stops) {

        // Creamos un arreglo con espacio para el depósito y todas las paradas.
        String[] routePoints = new String[stops.length + 1];

        // La primera posición siempre será el depósito.
        routePoints[0] = depotId;

        // Copiamos las paradas después del depósito.
        for (int i = 0; i < stops.length; i++) {
            routePoints[i + 1] = stops[i];
        }

        return routePoints;
    }

    /**
     * Construye el MST inducido por los puntos de ruta.
     *
     * Este MST no se construye sobre todo el grafo original.
     * Se construye solo sobre:
     *
     * depósito + paradas del camión
     *
     * El peso entre dos puntos no es necesariamente una arista directa del grafo,
     * sino la distancia mínima entre ellos obtenida desde Floyd-Warshall.
     *
     * Ejemplo:
     * routePoints = [A, C, D]
     *
     * Se crean aristas posibles:
     * A-C, A-D, C-D
     *
     * Luego se aplica una lógica tipo Kruskal:
     * - ordenar por peso
     * - agregar la arista si no forma ciclo
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param routePoints depósito + paradas.
     * @return arreglo de aristas que forman el MST inducido.
     */
    private static Edge[] buildInducedMST(
            Graph graph,
            int[][] distanceMatrix,
            String[] routePoints
    ) {

        // Si hay n puntos, el MST tendrá como máximo n - 1 aristas.
        Edge[] mstEdges = new Edge[routePoints.length - 1];

        // Creamos todas las aristas posibles entre los puntos de ruta.
        Edge[] allEdges = buildCompleteRouteEdges(graph, distanceMatrix, routePoints);

        // Ordenamos esas aristas por peso de menor a mayor.
        sortEdgesByWeight(allEdges);

        // UnionFind trabaja con índices numéricos.
        // Aquí los índices son posiciones dentro de routePoints.
        UnionFind unionFind = new UnionFind(routePoints.length);

        int mstEdgeCount = 0;

        // Recorremos las aristas de menor a mayor peso.
        for (int i = 0; i < allEdges.length; i++) {

            Edge currentEdge = allEdges[i];

            // Buscamos las posiciones de los extremos dentro de routePoints.
            int fromIndex = findRoutePointIndex(routePoints, currentEdge.getFrom());
            int toIndex = findRoutePointIndex(routePoints, currentEdge.getTo());

            // Si alguno no se encuentra, ignoramos la arista por seguridad.
            if (fromIndex == -1 || toIndex == -1) {
                continue;
            }

            /*
            * Si union retorna true, los puntos estaban en grupos diferentes.
            * Entonces la arista se puede agregar sin formar ciclo.
            */
            if (unionFind.union(fromIndex, toIndex)) {
                mstEdges[mstEdgeCount] = currentEdge;
                mstEdgeCount++;
            }

            // Si ya tenemos n - 1 aristas, el MST inducido está completo.
            if (mstEdgeCount == routePoints.length - 1) {
                break;
            }
        }

        return mstEdges;
    }

    /**
     * Crea todas las aristas posibles entre los puntos de ruta.
     *
     * Como MST-Based trabaja con depósito + paradas, necesitamos conectar
     * cada punto con todos los demás usando distancias mínimas.
     *
     * Si hay n puntos, la cantidad de aristas posibles en un grafo completo
     * no dirigido es:
     *
     * n * (n - 1) / 2
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param routePoints depósito + paradas.
     * @return arreglo con todas las aristas posibles entre los puntos.
     */
    private static Edge[] buildCompleteRouteEdges(
            Graph graph,
            int[][] distanceMatrix,
            String[] routePoints
    ) {

        int n = routePoints.length;
        int edgeCount = n * (n - 1) / 2;

        Edge[] edges = new Edge[edgeCount];

        int index = 0;

        // Usamos j = i + 1 para no repetir aristas.
        // Ejemplo: agregamos A-C, pero no C-A.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {

                int distance = getDistance(
                        graph,
                        distanceMatrix,
                        routePoints[i],
                        routePoints[j]
                );

                edges[index] = new Edge(routePoints[i], routePoints[j], distance);
                index++;
            }
        }

        return edges;
    }

    /**
     * Ordena un arreglo de aristas por peso de menor a mayor.
     *
     * Se usa para construir el MST inducido con lógica tipo Kruskal.
     *
     * @param edges arreglo de aristas.
     */
    private static void sortEdgesByWeight(Edge[] edges) {

        for (int i = 0; i < edges.length - 1; i++) {
            for (int j = 0; j < edges.length - 1 - i; j++) {

                if (edges[j].getWeight() > edges[j + 1].getWeight()) {
                    Edge temp = edges[j];
                    edges[j] = edges[j + 1];
                    edges[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Busca la posición de un punto dentro del arreglo routePoints.
     *
     * @param routePoints arreglo con depósito + paradas.
     * @param vertexId id del vértice buscado.
     * @return índice del punto o -1 si no existe.
     */
    private static int findRoutePointIndex(String[] routePoints, String vertexId) {

        for (int i = 0; i < routePoints.length; i++) {
            if (routePoints[i].equals(vertexId)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Genera el orden de visita usando DFS preorden sobre el MST inducido.
     *
     * El DFS empieza desde el depósito, que está en la posición 0 de routePoints.
     *
     * Ejemplo:
     * routePoints = [A, C, D]
     * mstEdges = [C-D, A-C]
     *
     * El árbol sería:
     * A -- C -- D
     *
     * DFS desde A:
     * A, C, D
     *
     * Para la ruta final no se guarda el depósito como parada,
     * solo se guardan las paradas visitadas después del depósito.
     *
     * @param routePoints arreglo con depósito + paradas.
     * @param mstEdges aristas del MST inducido.
     * @return arreglo con las paradas en orden DFS, sin incluir el depósito.
     */
    private static String[] getDFSOrderFromMST(String[] routePoints, Edge[] mstEdges) {

        // Arreglo para marcar cuáles puntos ya fueron visitados.
        boolean[] visited = new boolean[routePoints.length];

        // Como el depósito no se guarda como parada final,
        // el arreglo de resultado tiene tamaño routePoints.length - 1.
        String[] orderedStops = new String[routePoints.length - 1];

        // Usamos un arreglo de una posición para poder modificar el contador
        // dentro del método recursivo.
        int[] stopIndex = new int[1];
        stopIndex[0] = 0;

        // El depósito está en la posición 0, entonces el DFS inicia ahí.
        dfsMSTPreOrder(0, routePoints, mstEdges, visited, orderedStops, stopIndex);

        return orderedStops;
    }

    /**
     * Método recursivo para recorrer el MST inducido en preorden.
     *
     * Preorden significa:
     * 1. Visitar el vértice actual.
     * 2. Luego visitar sus vecinos no visitados.
     *
     * En este caso:
     * - Si el vértice actual es el depósito, no se agrega como parada.
     * - Si el vértice actual es una entrega, sí se agrega al orden de ruta.
     *
     * @param currentIndex índice actual dentro de routePoints.
     * @param routePoints arreglo con depósito + paradas.
     * @param mstEdges aristas del MST inducido.
     * @param visited arreglo de visitados.
     * @param orderedStops arreglo donde se guarda el orden final de paradas.
     * @param stopIndex contador de paradas agregadas.
     */
    private static void dfsMSTPreOrder(
            int currentIndex,
            String[] routePoints,
            Edge[] mstEdges,
            boolean[] visited,
            String[] orderedStops,
            int[] stopIndex
    ) {

        // Marcamos el punto actual como visitado.
        visited[currentIndex] = true;

        /*
        * Si currentIndex no es 0, significa que no es el depósito.
        * Entonces se agrega como parada de la ruta.
        *
        * routePoints[0] siempre es el depósito.
        */
        if (currentIndex != 0) {
            orderedStops[stopIndex[0]] = routePoints[currentIndex];
            stopIndex[0]++;
        }

        /*
        * Recorremos todas las aristas del MST inducido para encontrar
        * vecinos del punto actual.
        *
        * Como mstEdges representa un árbol no dirigido, una arista puede conectar:
        * actual -> vecino
        * o
        * vecino -> actual
        */
        for (int i = 0; i < mstEdges.length; i++) {

            Edge edge = mstEdges[i];

            // Seguridad por si alguna posición viene vacía.
            if (edge == null) {
                continue;
            }

            int neighborIndex = -1;

            /*
            * Si el origen de la arista es el punto actual,
            * entonces el destino es vecino.
            */
            if (edge.getFrom().equals(routePoints[currentIndex])) {
                neighborIndex = findRoutePointIndex(routePoints, edge.getTo());
            }

            /*
            * Si el destino de la arista es el punto actual,
            * entonces el origen es vecino.
            */
            else if (edge.getTo().equals(routePoints[currentIndex])) {
                neighborIndex = findRoutePointIndex(routePoints, edge.getFrom());
            }

            /*
            * Si se encontró un vecino válido y todavía no ha sido visitado,
            * hacemos DFS recursivo hacia ese vecino.
            */
            if (neighborIndex != -1 && !visited[neighborIndex]) {
                dfsMSTPreOrder(
                        neighborIndex,
                        routePoints,
                        mstEdges,
                        visited,
                        orderedStops,
                        stopIndex
                );
            }
        }
    }

    /**
     * Planifica una ruta usando la heurística basada en MST.
     *
     * Esta heurística funciona así:
     * 1. Obtiene las paradas únicas del camión.
     * 2. Construye el conjunto depósito + paradas.
     * 3. Construye el MST inducido usando las distancias mínimas de Floyd-Warshall.
     * 4. Recorre ese MST con DFS preorden desde el depósito.
     * 5. Usa ese orden DFS como ruta del camión.
     * 6. Calcula la distancia total:
     *    depósito -> paradas en orden -> depósito.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas calculada con Floyd-Warshall.
     * @param truck camión al que se le calculará la ruta.
     * @param depotId id del depósito.
     * @return resultado de la ruta calculada.
     */
    public static RouteResult mstBasedRoute(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        // Obtenemos las paradas únicas del camión.
        String[] stops = getUniqueStops(truck);

        // Creamos el resultado donde se guardará la ruta final.
        RouteResult result = new RouteResult(
                "MST-Based",
                truck.getId(),
                depotId,
                stops.length
        );

        // Si el camión no tiene paradas, la distancia total es 0.
        if (stops.length == 0) {
            result.setTotalDistance(0);
            return result;
        }

        // Construimos los puntos usados por la heurística:
        // depósito + paradas.
        String[] routePoints = buildRoutePoints(depotId, stops);

        // Construimos el MST inducido sobre esos puntos.
        Edge[] mstEdges = buildInducedMST(graph, distanceMatrix, routePoints);

        // Obtenemos el orden de visita usando DFS preorden sobre el MST inducido.
        String[] orderedStops = getDFSOrderFromMST(routePoints, mstEdges);

        // Guardamos las paradas obtenidas en el resultado.
        for (int i = 0; i < orderedStops.length; i++) {
            result.addStop(orderedStops[i]);
        }

        // Calculamos la distancia total de la ruta:
        // depósito -> paradas en orden -> depósito.
        int totalDistance = calculateRouteDistance(
                graph,
                distanceMatrix,
                depotId,
                result.getOrderedStops(),
                result.getStopCount()
        );

        // Guardamos la distancia total.
        result.setTotalDistance(totalDistance);

        return result;
    }

    /**
     * Compara las dos heurísticas de ruteo para un camión:
     * - Nearest Neighbor.
     * - MST-Based.
     *
     * Este método no crea una clase nueva de resultado.
     * Solo ejecuta ambas heurísticas, imprime sus rutas, compara las distancias
     * y muestra cuál fue mejor.
     *
     * También calcula el porcentaje de ahorro de MST-Based respecto a
     * Nearest Neighbor, porque el proyecto solicita reportar esa comparación.
     *
     * Fórmula de ahorro:
     *
     * ((distanciaNearestNeighbor - distanciaMSTBased) * 100) / distanciaNearestNeighbor
     *
     * Si el resultado es positivo, MST-Based ahorró distancia.
     * Si el resultado es 0, ambas heurísticas dieron la misma distancia.
     * Si el resultado es negativo, MST-Based fue peor que Nearest Neighbor.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas calculada con Floyd-Warshall.
     * @param truck camión al que se le comparan las rutas.
     * @param depotId id del depósito.
     */
    public static void compareRoutes(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        System.out.println("\n=================================");
        System.out.println("   COMPARACIÓN DE RUTAS | Camión " + truck.getId());
        System.out.println("=================================");

        // Calculamos la ruta usando Nearest Neighbor.
        RouteResult nearestNeighborResult =
                nearestNeighbor(graph, distanceMatrix, truck, depotId);

        // Calculamos la ruta usando la heurística basada en MST.
        RouteResult mstBasedResult =
                mstBasedRoute(graph, distanceMatrix, truck, depotId);

        // Imprimimos ambas rutas para comparar visualmente el orden de paradas.
        nearestNeighborResult.printRoute();
        mstBasedResult.printRoute();

        int nearestDistance = nearestNeighborResult.getTotalDistance();
        int mstDistance = mstBasedResult.getTotalDistance();

        System.out.println("\n--- COMPARACIÓN DE DISTANCIAS ---");
        System.out.println("Nearest Neighbor: " + nearestDistance + "m");
        System.out.println("MST-Based: " + mstDistance + "m");

        /*
        * Si nearestDistance es 0, significa que el camión no tenía paradas.
        * En ese caso no se puede calcular porcentaje de ahorro dividiendo entre 0.
        */
        if (nearestDistance == 0) {
            System.out.println("El camión no tiene paradas asignadas.");
            System.out.println("Ahorro MST-Based sobre Nearest Neighbor: 0.0%");
            return;
        }

        // Calculamos el ahorro porcentual de MST-Based respecto a Nearest Neighbor.
        double savingPercentage =
                ((nearestDistance - mstDistance) * 100.0) / nearestDistance;

        System.out.println("Ahorro MST-Based sobre Nearest Neighbor: "
                + savingPercentage + "%");

        // Determinamos cuál heurística produjo menor distancia.
        if (nearestDistance < mstDistance) {
            System.out.println("Mejor heurística: Nearest Neighbor");
        } else if (mstDistance < nearestDistance) {
            System.out.println("Mejor heurística: MST-Based");
        } else {
            System.out.println("Resultado: ambas heurísticas tienen la misma distancia.");
        }
    }

    /**
     * Imprime el reporte final de planificación para un camión.
     *
     * Este reporte junta la información que pide el proyecto:
     * - Paquetes asignados al camión.
     * - Carga total transportada.
     * - Porcentaje de ocupación.
     * - Ruta calculada con Nearest Neighbor.
     * - Ruta calculada con MST-Based.
     * - Mejor ruta según menor distancia.
     * - Ahorro porcentual de MST-Based sobre Nearest Neighbor.
     *
     * Este método no reemplaza a la interfaz gráfica.
     * Solo deja una salida clara para pruebas, defensa y documentación.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas calculada con Floyd-Warshall.
     * @param truck camión que se desea reportar.
     * @param depotId id del depósito.
     */
    public static void printFinalTruckReport(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        System.out.println("\n=================================");
        System.out.println("   REPORTE FINAL | Camión " + truck.getId());
        System.out.println("=================================");

        // Información de carga del camión.
        System.out.println("Carga transportada: "
                + truck.getCurrentLoad() + "kg / "
                + truck.getMaxCapacity() + "kg");

        System.out.println("Porcentaje de ocupación: "
                + truck.getOccupancyPercentage() + "%");

        // Paquetes asignados al camión.
        System.out.println("\n--- PAQUETES ASIGNADOS ---");

        Package[] assignedPackages = truck.getAssignedPackages();

        if (truck.getAssignedPackageCount() == 0) {
            System.out.println("El camión no tiene paquetes asignados.");
        } else {
            for (int i = 0; i < truck.getAssignedPackageCount(); i++) {
                System.out.println(assignedPackages[i]);
            }
        }

        // Calculamos ambas rutas.
        RouteResult nearestNeighborResult =
                nearestNeighbor(graph, distanceMatrix, truck, depotId);

        RouteResult mstBasedResult =
                mstBasedRoute(graph, distanceMatrix, truck, depotId);

        // Mostramos ambas rutas para que se puedan comparar.
        System.out.println("\n--- RUTAS CALCULADAS ---");
        nearestNeighborResult.printRoute();
        mstBasedResult.printRoute();

        int nearestDistance = nearestNeighborResult.getTotalDistance();
        int mstDistance = mstBasedResult.getTotalDistance();

        // Calculamos el ahorro de MST-Based respecto a Nearest Neighbor.
        double savingPercentage = 0.0;

        if (nearestDistance != 0) {
            savingPercentage =
                    ((nearestDistance - mstDistance) * 100.0) / nearestDistance;
        }

        System.out.println("\n--- COMPARACIÓN FINAL ---");
        System.out.println("Distancia Nearest Neighbor: " + nearestDistance + "m");
        System.out.println("Distancia MST-Based: " + mstDistance + "m");
        System.out.println("Ahorro MST-Based sobre Nearest Neighbor: "
                + savingPercentage + "%");

        /*
        * Se escoge como mejor ruta la que tenga menor distancia total.
        * Si ambas tienen la misma distancia, se deja constancia del empate.
        */
        if (nearestDistance < mstDistance) {
            System.out.println("Mejor heurística: Nearest Neighbor");
            System.out.println("Ruta final recomendada:");
            nearestNeighborResult.printRoute();
        } else if (mstDistance < nearestDistance) {
            System.out.println("Mejor heurística: MST-Based");
            System.out.println("Ruta final recomendada:");
            mstBasedResult.printRoute();
        } else {
            System.out.println("Resultado: ambas heurísticas tienen la misma distancia.");
            System.out.println("Ruta final recomendada:");
            nearestNeighborResult.printRoute();
        }
    }

    /**
     * Método temporal de prueba.
     *
     * Imprime las paradas únicas de un camión.
     *
     * @param truck camión que se desea revisar.
     */
    public static void printUniqueStopsForTest(Truck truck) {

        String[] stops = getUniqueStops(truck);

        System.out.println("\n--- PARADAS ÚNICAS DEL CAMIÓN " + truck.getId() + " ---");

        if (stops.length == 0) {
            System.out.println("El camión no tiene paradas.");
            return;
        }

        for (int i = 0; i < stops.length; i++) {
            System.out.println(stops[i]);
        }
    }

    /**
     * Método temporal de prueba 2
     *
     * Imprime los puntos que se usarían para la heurística MST-Based:
     * depósito + paradas únicas del camión.
     *
     * @param truck camión que se desea revisar.
     * @param depotId id del depósito.
     */
    public static void printRoutePointsForTest(Truck truck, String depotId) {

        String[] stops = getUniqueStops(truck);
        String[] routePoints = buildRoutePoints(depotId, stops);

        System.out.println("\n--- PUNTOS PARA MST-BASED | Camión " + truck.getId() + " ---");

        for (int i = 0; i < routePoints.length; i++) {
            System.out.println(routePoints[i]);
        }
    }

    /**
     * Método temporal de prueba 3
     *
     * Imprime el MST inducido usado por la heurística MST-Based.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de Floyd-Warshall.
     * @param truck camión.
     * @param depotId id del depósito.
     */
    public static void printInducedMSTForTest(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        String[] stops = getUniqueStops(truck);
        String[] routePoints = buildRoutePoints(depotId, stops);

        Edge[] mstEdges = buildInducedMST(graph, distanceMatrix, routePoints);

        System.out.println("\n--- MST INDUCIDO | Camión " + truck.getId() + " ---");

        for (int i = 0; i < mstEdges.length; i++) {
            System.out.println(mstEdges[i]);
        }
    }
    /**
     * Método temporal de prueba 4
     *
     * Imprime el orden DFS preorden obtenido desde el MST inducido.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de Floyd-Warshall.
     * @param truck camión.
     * @param depotId id del depósito.
     */
    public static void printMSTDFSOrderForTest(
            Graph graph,
            int[][] distanceMatrix,
            Truck truck,
            String depotId
    ) {

        String[] stops = getUniqueStops(truck);
        String[] routePoints = buildRoutePoints(depotId, stops);
        Edge[] mstEdges = buildInducedMST(graph, distanceMatrix, routePoints);

        String[] orderedStops = getDFSOrderFromMST(routePoints, mstEdges);

        System.out.println("\n--- ORDEN DFS MST-BASED | Camión " + truck.getId() + " ---");

        System.out.print(depotId);

        for (int i = 0; i < orderedStops.length; i++) {
            System.out.print(" -> " + orderedStops[i]);
        }

        System.out.println(" -> " + depotId);
    }
}