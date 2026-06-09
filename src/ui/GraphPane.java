package ui;

import algorithms.MSTResult;
import graph.AdjacencyNode;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import model.Package;
import model.RouteResult;
import model.Truck;
import structures.MyLinkedList;
import structures.Node;
import model.ShortestPathResult;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Clase GraphPane.
 *
 * Esta clase dibuja visualmente el mapa de LogisTEC.
 *
 * Dibuja:
 * - Calles del grafo.
 * - MST.
 * - Rutas recomendadas por camión.
 * - Depósito.
 * - Intersecciones.
 * - Casas como puntos de entrega.
 * - Paquetes pendientes o rechazados.
 *
 * No ejecuta algoritmos.
 * Solo representa gráficamente los datos ya calculados.
 */
public class GraphPane extends Canvas {

    // Grafo de la ciudad.
    private Graph graph;

    // Rutas recomendadas para los camiones.
    private RouteResult[] routes;

    // Resultado del MST que se desea mostrar.
    private MSTResult mstResult;

    // Paquetes originales cargados desde el JSON.
    private Package[] packages;

    // Camiones con paquetes ya asignados.
    private Truck[] trucks;

    // Resultado de Dijkstra que se desea resaltar en el mapa.
    private ShortestPathResult shortestPathResult;

    /**
     * Constructor de GraphPane.
     *
     * @param graph grafo de la ciudad.
     * @param routes rutas recomendadas.
     * @param mstResult resultado del MST.
     * @param packages paquetes cargados.
     * @param trucks camiones con paquetes asignados.
     */
    public GraphPane(
            Graph graph,
            RouteResult[] routes,
            MSTResult mstResult,
            Package[] packages,
            Truck[] trucks
    ) {
        super(770, 700);

        this.graph = graph;
        this.routes = routes;
        this.mstResult = mstResult;
        this.packages = packages;
        this.trucks = trucks;

        drawGraph();
    }

    /**
     * Actualiza el camino mínimo que debe resaltarse en el grafo.
     *
     * Este metodo se llama desde InfoPanel cuando el usuario presiona
     * el botón "Calcular camino mínimo".
     *
     * @param shortestPathResult resultado de Dijkstra.
     */
    public void setShortestPathResult(ShortestPathResult shortestPathResult) {
        this.shortestPathResult = shortestPathResult;

        /*
         * Se vuelve a dibujar el grafo para que aparezca el nuevo camino.
         */
        drawGraph();
    }
    /**
     * Dibuja el camino mínimo consultado con Dijkstra.
     * El camino se muestra con una línea celeste gruesa.
     * @param gc contexto gráfico.
     */
    private void drawShortestPath(GraphicsContext gc) {
        if (shortestPathResult == null || !shortestPathResult.isReachable()) {
            return;
        }

        String[] path = shortestPathResult.getPath();

        gc.setStroke(Color.CYAN);
        gc.setLineWidth(7);
        gc.setGlobalAlpha(0.9);

        /*
         * Se dibuja cada segmento del camino:
         * path[0] -> path[1] -> path[2] -> ...
         */
        for (int i = 0; i < shortestPathResult.getPathCount() - 1; i++) {
            Vertex fromVertex = graph.getVertexById(path[i]);
            Vertex toVertex = graph.getVertexById(path[i + 1]);

            if (fromVertex == null || toVertex == null) {
                continue;
            }

            gc.strokeLine(
                    fromVertex.getX(),
                    fromVertex.getY(),
                    toVertex.getX(),
                    toVertex.getY()
            );
        }

        gc.setGlobalAlpha(1.0);
    }

    /**
     * Dibuja todo el mapa.
     */
    public void drawGraph() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.clearRect(0, 0, getWidth(), getHeight());

        drawBackground(gc);
        drawEdges(gc);
        drawMST(gc);
        drawRoutes(gc);
        drawShortestPath(gc);
        drawVertices(gc);
        drawLegend(gc);
    }

    /**
     * Dibuja el fondo del mapa.
     *
     * @param gc contexto gráfico.
     */
    private void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.rgb(232, 245, 233));
        gc.fillRect(0, 0, getWidth(), getHeight());

        /*
         * Se dibuja una cuadrícula suave para que parezca un plano de ciudad.
         */
        gc.setStroke(Color.rgb(210, 225, 210));
        gc.setLineWidth(1);

        for (int x = 0; x < getWidth(); x += 50) {
            gc.strokeLine(x, 0, x, getHeight());
        }

        for (int y = 0; y < getHeight(); y += 50) {
            gc.strokeLine(0, y, getWidth(), y);
        }
    }

    /**
     * Dibuja todas las aristas del grafo.
     *
     * Las aristas representan calles.
     *
     * @param gc contexto gráfico.
     */
    private void drawEdges(GraphicsContext gc) {
        gc.setStroke(Color.rgb(170, 170, 170));
        gc.setLineWidth(3);

        for (int i = 0; i < graph.getVertexCount(); i++) {
            Vertex fromVertex = graph.getVertex(i);

            if (fromVertex == null) {
                continue;
            }

            MyLinkedList<AdjacencyNode> neighbors =
                    graph.getNeighbors(fromVertex.getId());

            if (neighbors == null) {
                continue;
            }

            Node<AdjacencyNode> current = neighbors.getHead();

            while (current != null) {
                AdjacencyNode neighbor = current.getData();

                int toIndex = graph.getVertexIndex(neighbor.getDestination());

                /*
                 * Se evita dibujar dos veces la misma arista.
                 */
                if (toIndex != -1 && i < toIndex) {
                    Vertex toVertex = graph.getVertex(toIndex);

                    gc.strokeLine(
                            fromVertex.getX(),
                            fromVertex.getY(),
                            toVertex.getX(),
                            toVertex.getY()
                    );
                }

                current = current.getNext();
            }
        }
    }

    /**
     * Dibuja el MST sobre el grafo.
     *
     * El MST se dibuja con líneas moradas punteadas.
     *
     * @param gc contexto gráfico.
     */
    private void drawMST(GraphicsContext gc) {
        if (mstResult == null) {
            return;
        }

        gc.setStroke(Color.rgb(123, 31, 162));
        gc.setLineWidth(3);
        gc.setLineDashes(12, 8);

        Edge[] mstEdges = mstResult.getMstEdges();

        for (int i = 0; i < mstResult.getEdgeCount(); i++) {
            Edge edge = mstEdges[i];

            Vertex fromVertex = graph.getVertexById(edge.getFrom());
            Vertex toVertex = graph.getVertexById(edge.getTo());

            if (fromVertex == null || toVertex == null) {
                continue;
            }

            gc.strokeLine(
                    fromVertex.getX(),
                    fromVertex.getY(),
                    toVertex.getX(),
                    toVertex.getY()
            );
        }

        /*
         * Se eliminan los guiones para que las siguientes líneas se dibujen normales.
         */
        gc.setLineDashes();
    }

    /**
     * Dibuja las rutas recomendadas para los camiones.
     *
     * Cada camión se dibuja con un color distinto.
     *
     * @param gc contexto gráfico.
     */
    private void drawRoutes(GraphicsContext gc) {
        if (routes == null) {
            return;
        }

        Color[] routeColors = {
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.ORANGE,
                Color.DEEPPINK
        };

        for (int i = 0; i < routes.length; i++) {
            RouteResult route = routes[i];

            if (route == null || route.getStopCount() == 0) {
                continue;
            }

            gc.setStroke(routeColors[i % routeColors.length]);
            gc.setLineWidth(5);
            gc.setGlobalAlpha(0.85);

            String currentId = route.getDepotId();
            String[] stops = route.getOrderedStops();

            for (int j = 0; j < route.getStopCount(); j++) {
                drawRouteSegment(gc, currentId, stops[j]);
                currentId = stops[j];
            }

            drawRouteSegment(gc, currentId, route.getDepotId());

            gc.setGlobalAlpha(1.0);
        }
    }

    /**
     * Dibuja un segmento de ruta entre dos puntos.
     *
     * Visualmente es una línea recta entre paradas.
     * La distancia usada por la ruta viene de Floyd-Warshall.
     *
     * @param gc contexto gráfico.
     * @param fromId origen.
     * @param toId destino.
     */
    private void drawRouteSegment(GraphicsContext gc, String fromId, String toId) {
        Vertex fromVertex = graph.getVertexById(fromId);
        Vertex toVertex = graph.getVertexById(toId);

        if (fromVertex == null || toVertex == null) {
            return;
        }

        gc.strokeLine(
                fromVertex.getX(),
                fromVertex.getY(),
                toVertex.getX(),
                toVertex.getY()
        );
    }

    /**
     * Dibuja todos los vértices.
     *
     * @param gc contexto gráfico.
     */
    private void drawVertices(GraphicsContext gc) {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            Vertex vertex = graph.getVertex(i);

            if (vertex == null) {
                continue;
            }

            if (vertex.isDepot()) {
                drawDepot(gc, vertex);
            } else if (vertex.getType().equalsIgnoreCase("ENTREGA")) {
                drawHouse(gc, vertex);
            } else {
                drawIntersection(gc, vertex);
            }

            drawPendingMarkerIfNeeded(gc, vertex);
            drawVertexLabel(gc, vertex);
        }
    }

    /**
     * Dibuja el depósito como una bodega.
     *
     * @param gc contexto gráfico.
     * @param vertex vértice depósito.
     */
    private void drawDepot(GraphicsContext gc, Vertex vertex) {
        double x = vertex.getX();
        double y = vertex.getY();

        gc.setFill(Color.rgb(183, 28, 28));
        gc.fillRect(x - 18, y - 12, 36, 26);

        gc.setFill(Color.rgb(120, 20, 20));
        gc.fillPolygon(
                new double[]{x - 22, x, x + 22},
                new double[]{y - 12, y - 32, y - 12},
                3
        );

        gc.setFill(Color.WHITE);
        gc.fillRect(x - 6, y + 2, 12, 12);
    }

    /**
     * Dibuja una casa para representar un punto de entrega.
     *
     * @param gc contexto gráfico.
     * @param vertex vértice de entrega.
     */
    private void drawHouse(GraphicsContext gc, Vertex vertex) {
        double x = vertex.getX();
        double y = vertex.getY();

        gc.setFill(Color.rgb(46, 125, 50));
        gc.fillRect(x - 14, y - 4, 28, 22);

        gc.setFill(Color.rgb(27, 94, 32));
        gc.fillPolygon(
                new double[]{x - 18, x, x + 18},
                new double[]{y - 4, y - 24, y - 4},
                3
        );

        gc.setFill(Color.rgb(230, 245, 230));
        gc.fillRect(x - 4, y + 6, 8, 12);
    }

    /**
     * Dibuja una intersección normal.
     *
     * @param gc contexto gráfico.
     * @param vertex vértice intersección.
     */
    private void drawIntersection(GraphicsContext gc, Vertex vertex) {
        double radius = 12;

        gc.setFill(Color.rgb(38, 70, 83));
        gc.fillOval(
                vertex.getX() - radius,
                vertex.getY() - radius,
                radius * 2,
                radius * 2
        );
    }

    /**
     * Dibuja un marcador si el vértice tiene paquetes pendientes.
     *
     * Un paquete se considera pendiente si no aparece en ningún camión.
     *
     * @param gc contexto gráfico.
     * @param vertex vértice revisado.
     */
    private void drawPendingMarkerIfNeeded(GraphicsContext gc, Vertex vertex) {
        int pendingCount = countPendingPackagesForVertex(vertex.getId());

        if (pendingCount == 0) {
            return;
        }

        gc.setFill(Color.GOLD);
        gc.fillOval(vertex.getX() + 10, vertex.getY() - 25, 20, 20);

        gc.setFill(Color.BLACK);
        gc.fillText(String.valueOf(pendingCount), vertex.getX() + 16, vertex.getY() - 10);
    }

    /**
     * Dibuja el nombre del vértice.
     *
     * @param gc contexto gráfico.
     * @param vertex vértice.
     */
    private void drawVertexLabel(GraphicsContext gc, Vertex vertex) {
        gc.setFill(Color.BLACK);
        gc.fillText(vertex.getId(), vertex.getX() - 10, vertex.getY() - 30);
    }

    /**
     * Cuenta cuántos paquetes pendientes tiene un vértice.
     *
     * @param vertexId id del vértice.
     * @return cantidad de paquetes pendientes.
     */
    private int countPendingPackagesForVertex(String vertexId) {
        int count = 0;

        for (int i = 0; i < packages.length; i++) {
            Package currentPackage = packages[i];

            if (currentPackage.getDestinationVertexId().equals(vertexId)
                    && !isPackageAssigned(currentPackage)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Verifica si un paquete ya fue asignado a algún camión.
     *
     * @param packageToFind paquete buscado.
     * @return true si el paquete está asignado.
     */
    private boolean isPackageAssigned(Package packageToFind) {
        for (int i = 0; i < trucks.length; i++) {
            Package[] assignedPackages = trucks[i].getAssignedPackages();

            for (int j = 0; j < trucks[i].getAssignedPackageCount(); j++) {
                if (assignedPackages[j].getId().equals(packageToFind.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Dibuja la leyenda de colores.
     *
     * La leyenda explica:
     * - Qué figura representa cada tipo de vértice.
     * - Qué línea representa el MST.
     * - Qué color de ruta pertenece a cada camión.
     * - Qué significa un paquete pendiente.
     *
     * @param gc contexto gráfico.
     */
    private void drawLegend(GraphicsContext gc) {
        double x = 540;
        double y = 25;

        /*
         * Se aumenta el tamaño del panel para que todo el contenido
         * quede dentro del cuadro sin verse apretado.
         */
        double width = 220;
        double height = 285;

        /*
         * Fondo del cuadro de leyenda.
         */
        gc.setFill(Color.rgb(255, 255, 255, 0.95));
        gc.fillRoundRect(x, y, width, height, 12, 12);

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, width, height, 12, 12);

        /*
         * Contiene el titulo dl cuadro con información
         */
        gc.setFill(Color.BLACK);
        gc.fillText("Información relevante", x + 15, y + 22);

        /*
         * Depósito.
         */
        gc.setFill(Color.rgb(183, 28, 28));
        gc.fillRect(x + 15, y + 40, 18, 14);
        gc.setFill(Color.BLACK);
        gc.fillText("Deposito", x + 45, y + 52);

        /*
         * Casa o entrega.
         */
        gc.setFill(Color.rgb(46, 125, 50));
        gc.fillRect(x + 15, y + 66, 18, 14);
        gc.setFill(Color.BLACK);
        gc.fillText("Casa / entrega", x + 45, y + 78);

        /*
         * Intersección.
         */
        gc.setFill(Color.rgb(38, 70, 83));
        gc.fillOval(x + 15, y + 91, 18, 18);
        gc.setFill(Color.BLACK);
        gc.fillText("Interseccion", x + 45, y + 105);

        /*
         * MST.
         */
        gc.setStroke(Color.rgb(123, 31, 162));
        gc.setLineWidth(3);
        gc.setLineDashes(8, 6);
        gc.strokeLine(x + 15, y + 126, x + 38, y + 126);
        gc.setLineDashes();

        gc.setFill(Color.BLACK);
        gc.fillText("MST", x + 45, y + 130);

        /*
         * Paquete pendiente.
         */
        gc.setFill(Color.GOLD);
        gc.fillOval(x + 15, y + 142, 18, 18);
        gc.setFill(Color.BLACK);
        gc.fillText("Paquete pendiente", x + 45, y + 156);

        /*
         * Subtítulo para las rutas.
         */
        gc.setFill(Color.BLACK);
        gc.fillText("Rutas de camiones", x + 15, y + 182);

        /*
         * Ruta C01.
         */
        gc.setStroke(Color.RED);
        gc.setLineWidth(4);
        gc.strokeLine(x + 15, y + 200, x + 42, y + 200);
        gc.setFill(Color.BLACK);
        gc.fillText("Ruta C01", x + 52, y + 204);

        /*
         * Ruta C02.
         */
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(4);
        gc.strokeLine(x + 15, y + 222, x + 42, y + 222);
        gc.setFill(Color.BLACK);
        gc.fillText("Ruta C02", x + 52, y + 226);

        /*
         * Ruta C03.
         */
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(4);
        gc.strokeLine(x + 15, y + 244, x + 42, y + 244);
        gc.setFill(Color.BLACK);
        gc.fillText("Ruta C03", x + 52, y + 248);
    }
}