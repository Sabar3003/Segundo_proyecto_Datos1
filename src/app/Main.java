package app;

import graph.Graph;
import graph.Vertex;
import structures.MyPriorityQueue;
import structures.UnionFind;

/**
 * Clase principal del proyecto.
 *
 * Por ahora solo se usa para probar que:
 * - Se pueden crear vértices.
 * - Se pueden crear aristas.
 * - El grafo guarda conexiones en ambos sentidos.
 * - Se puede imprimir la lista de adyacencia.
 */
public class Main {

    public static void main(String[] args) {

        /*
         * Creamos un grafo con capacidad máxima de 10 vértices.
         *
         * Este número es solo para pruebas iniciales.
         * Más adelante, cuando se cargue desde JSON,
         * se puede crear con la cantidad real de vértices.
         */
        Graph graph = new Graph(10);

        /*
         * Agregamos vértices.
         *
         * A será el depósito.
         * B y C serán intersecciones.
         * D y E serán puntos de entrega.
         */
        graph.addVertex(new Vertex("A", "DEPOT", 100, 100));
        graph.addVertex(new Vertex("B", "INTERSECCION", 200, 120));
        graph.addVertex(new Vertex("C", "INTERSECCION", 300, 160));
        graph.addVertex(new Vertex("D", "ENTREGA", 400, 200));
        graph.addVertex(new Vertex("E", "ENTREGA", 500, 250));

        /*
         * Agregamos aristas.
         *
         * Como el grafo es no dirigido, cuando agregamos A-B,
         * internamente también se guarda B-A.
         */
        graph.addEdge("A", "B", 320);
        graph.addEdge("B", "C", 250);
        graph.addEdge("C", "D", 180);
        graph.addEdge("D", "E", 210);
        graph.addEdge("A", "E", 900);

        /*
         * Imprimimos el grafo para revisar la lista de adyacencia.
         */
        graph.printGraph();

        /*
         * Probamos el método getWeight.
         *
         * A-B sí existe directamente, entonces debe retornar 320.
         * A-C no existe directamente, entonces debe retornar -1.
         */
        System.out.println();
        System.out.println("Peso entre A y B: " + graph.getWeight("A", "B"));
        System.out.println("Peso entre A y C: " + graph.getWeight("A", "C"));

        /*
         * Prueba de cola de prioridad.
         *
         * Debe extraer primero el elemento con menor prioridad.
         */
        System.out.println();
        System.out.println("Prueba MyPriorityQueue:");
        MyPriorityQueue<String> pq = new MyPriorityQueue<>(10);

        pq.insert("A", 30);
        pq.insert("B", 10);
        pq.insert("C", 20);

        System.out.println(pq.extractMin()); // Debe imprimir B
        System.out.println(pq.extractMin()); // Debe imprimir C
        System.out.println(pq.extractMin()); // Debe imprimir A

        /*
         * Prueba de UnionFind.
         *
         * Sirve para verificar si dos elementos pertenecen al mismo conjunto.
         */
        System.out.println();
        System.out.println("Prueba UnionFind:");
        UnionFind uf = new UnionFind(5);

        uf.union(0, 1);
        uf.union(1, 2);

        System.out.println(uf.connected(0, 2)); // true
        System.out.println(uf.connected(0, 4)); // false

        // PRUEBAS DE BFS Y DFS
        System.out.println("\n=================================");
        System.out.println("   PRUEBAS DE RECORRIDOS   ");
        System.out.println("=================================");
        // Ejecutamos desde el nodo inicial "A" (El depósito)
        System.out.println("Ejecutando desde el depósito (A):");
        algorithms.GraphAlgorithms.bfs(graph, "A");
        algorithms.GraphAlgorithms.dfs(graph, "A");

        // PRUEBAS DE DIJKSTRA
        algorithms.GraphAlgorithms.dijkstra(graph, "A"); // Ejecutamos Dijkstra desde el depósito central "A"

        // PRUEBAS DE FLOYD-WARSHALL
        algorithms.GraphAlgorithms.floydWarshall(graph);

        System.out.println();
        // PRUEBAS DE OBTENER TODAS LAS ARISTAS DEL GRAFO
        // Agregado por integrante 3
        algorithms.MSTAlgorithms.printAllEdgesForTest(graph);

        System.out.println();
        // PRUEBAS DE OBTENER TODAS LAS ARISTAS ORDENADAS POR PESO
        // Agregado por integrante 3
        algorithms.MSTAlgorithms.printSortedEdgesForTest(graph);

        System.out.println();
        // PRUEBAS DE PRIM Y KRUSKAL
        // Agregado por integrante 3
        algorithms.MSTAlgorithms.comparePrimAndKruskal(graph, "A");

        // PRUEBAS DE WARSHALL CON VÉRTICE INALCANZABLE
        // Cambiado por integrante 3
        // Agregamos un vértice "X" que no tiene conexiones, para probar que Warshall lo detecta como inaccesible desde "A".
        // Va de ultimo para que no afecte las pruebas anteriores.
        graph.addVertex(new Vertex("X", "ENTREGA", 600, 600));
        algorithms.GraphAlgorithms.warshall(graph, "A");

        // PRUEBAS DE ASIGNACIÓN DE PAQUETES A CAMIONES
        // Agregado por integrante 3
        model.Package[] packages = new model.Package[6];

        packages[0] = new model.Package("P01", "D", 5, 1);
        packages[1] = new model.Package("P02", "E", 8, 2);
        packages[2] = new model.Package("P03", "C", 12, 1);
        packages[3] = new model.Package("P04", "D", 4, 3);
        packages[4] = new model.Package("P05", "E", 20, 2);
        packages[5] = new model.Package("P06", "C", 7, 1);

        model.Truck[] trucks = new model.Truck[2];

        trucks[0] = new model.Truck("C01", 15, packages.length);
        trucks[1] = new model.Truck("C02", 20, packages.length);

        model.AssignmentResult assignmentResult =
                planner.PackageAssigner.assignPackages(packages, trucks);

        assignmentResult.printReport();

        System.out.println();
        // PRUEBAS DE OBTENER PARADAS ÚNICAS DE UN CAMIÓN
        // Agregado por integrante 3
        planner.RoutePlanner.printUniqueStopsForTest(trucks[0]);
        planner.RoutePlanner.printUniqueStopsForTest(trucks[1]);

        // PRUEBAS DE RUTEADOR VECINO MÁS CERCANO
        // Agregado por integrante 3
        int[][] distanceMatrix = algorithms.GraphAlgorithms.getFloydWarshallDistanceMatrix(graph);

        planner.RoutePlanner.compareRoutes(graph, distanceMatrix, trucks[0], "A");
        planner.RoutePlanner.compareRoutes(graph, distanceMatrix, trucks[1], "A");

        // PRUEBAS DE OBTENER EL REPORTE FINAL DE RUTA PARA UN CAMIÓN
        // Agregado por integrante 3
        planner.RoutePlanner.printFinalTruckReport(graph, distanceMatrix, trucks[0], "A");
        planner.RoutePlanner.printFinalTruckReport(graph, distanceMatrix, trucks[1], "A");

        // PRUEBAS DE OBTENER LOS PUNTOS DE RUTA EN ORDEN PARA UN CAMIÓN
        // Agregado por integrante 3
        planner.RoutePlanner.printRoutePointsForTest(trucks[0], "A");
        planner.RoutePlanner.printRoutePointsForTest(trucks[1], "A");

        // PRUEBAS DE OBTENER EL MST INDUCIDO POR LOS PUNTOS DE RUTA DE UN CAMIÓN
        // Agregado por integrante 3
        planner.RoutePlanner.printInducedMSTForTest(graph, distanceMatrix, trucks[0], "A");
        planner.RoutePlanner.printInducedMSTForTest(graph, distanceMatrix, trucks[1], "A");

        // PRUEBAS DE OBTENER EL ORDEN DFS PREORDEN DESDE EL MST INDUCIDO
        // Agregado por integrante 3
        planner.RoutePlanner.printMSTDFSOrderForTest(graph, distanceMatrix, trucks[0], "A");
        planner.RoutePlanner.printMSTDFSOrderForTest(graph, distanceMatrix, trucks[1], "A");
    }
}