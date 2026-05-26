package app;

import graph.Graph;
import graph.Vertex;

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
    }
}