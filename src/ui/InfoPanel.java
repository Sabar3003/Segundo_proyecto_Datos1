package ui;

import algorithms.MSTResult;
import graph.Graph;
import io.LogisticsData;
import model.Package;
import model.RouteResult;
import model.Truck;
import algorithms.GraphAlgorithms;
import model.ShortestPathResult;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.RouteComparisonResult;

/**
 * Clase InfoPanel.
 *
 * Este panel muestra la información textual de la simulación:
 * - Resumen general del caso.
 * - Información del MST.
 * - Estado de los camiones.
 * - Rutas recomendadas.
 * - Paquetes pendientes.
 *
 * La clase no ejecuta algoritmos.
 * Solo presenta información ya calculada en otras partes del sistema.
 */
public class InfoPanel extends ScrollPane {

    /**
     * Constructor de InfoPanel.
     *
     * @param data datos cargados desde JSON.
     * @param routes rutas recomendadas para los camiones.
     * @param mstResult resultado del MST que se muestra en el grafo.
     */
    public InfoPanel(
            LogisticsData data,
            RouteResult[] routes,
            MSTResult mstResult,
            GraphPane graphPane,
            RouteComparisonResult[] routeComparisons
    ) {

        /*
         * VBox funciona como una columna vertical.
         * Dentro se agregan las secciones del panel derecho.
         */
        VBox content = new VBox(14);

        /*
         * Estilo general del panel derecho.
         * Se usa un fondo claro para que se vea más limpio.
         */
        content.setStyle(
                "-fx-padding: 20 20 90 20;" +
                        "-fx-background-color: #ECEFF1;"
        );

        addHeader(content);
        addLegend(content);
        addGeneralSummary(content, data);
        addShortestPathQuery(content, data, graphPane);
        addMSTSummary(content, mstResult);
        addTrucks(content, data.getTrucks());
        addRouteComparisons(content, routeComparisons);
        addUnassignedPackages(content, data);

        /*
         * Configuración del ScrollPane.
         * Esto permite bajar correctamente hasta el final del panel.
         */
        setContent(content);
        setFitToWidth(true);
        setPrefWidth(460);
        setMinWidth(460);
        setMaxWidth(460);

        setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setPannable(true);

        setStyle(
                "-fx-background: #ECEFF1;" +
                        "-fx-background-color: #ECEFF1;" +
                        "-fx-border-color: transparent;"
        );
    }

    /**
     * Agrega una sección para consultar el camino mínimo con Dijkstra.
     *
     * El usuario selecciona:
     * - Origen.
     * - Destino.
     *
     * Luego presiona un botón y se muestra:
     * - Camino mínimo.
     * - Distancia total.
     *
     * Además, el camino se resalta visualmente en el GraphPane.
     *
     * @param content contenedor principal.
     * @param data datos del proyecto.
     * @param graphPane panel del grafo que se debe actualizar.
     */
    private void addShortestPathQuery(
            VBox content,
            LogisticsData data,
            GraphPane graphPane
    ) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Consulta de camino mínimo"));

        ComboBox<String> originComboBox = new ComboBox<>();
        ComboBox<String> destinationComboBox = new ComboBox<>();

        /*
         * Se llenan los selectores con todos los vértices del grafo.
         */
        Graph graph = data.getGraph();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            String vertexId = graph.getVertex(i).getId();

            originComboBox.getItems().add(vertexId);
            destinationComboBox.getItems().add(vertexId);
        }

        /*
         * Valores por defecto para que sea más cómodo probar.
         */
        originComboBox.setValue(data.getDepotId());

        if (graph.getVertexCount() > 1) {
            destinationComboBox.setValue(graph.getVertex(1).getId());
        }

        originComboBox.setMaxWidth(Double.MAX_VALUE);
        destinationComboBox.setMaxWidth(Double.MAX_VALUE);

        Label originLabel = createText("Origen:");
        Label destinationLabel = createText("Destino:");

        Label resultLabel = createRouteText(
                "Seleccione origen y destino, luego presione Calcular camino mínimo."
        );

        Button calculateButton = new Button("Calcular camino mínimo");

        calculateButton.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #1565C0;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8;" +
                        "-fx-background-radius: 8;"
        );

        /*
         * Acción del botón.
         * Aquí se ejecuta Dijkstra para el origen y destino seleccionados.
         */
        calculateButton.setOnAction(event -> {
            String originId = originComboBox.getValue();
            String destinationId = destinationComboBox.getValue();

            ShortestPathResult result =
                    GraphAlgorithms.shortestPath(graph, originId, destinationId);

            /*
             * Se actualiza el dibujo del grafo para resaltar el camino.
             */
            graphPane.setShortestPathResult(result);

            if (!result.isReachable()) {
                resultLabel.setText(
                        "No existe camino entre "
                                + originId + " y " + destinationId + "."
                );
                return;
            }

            resultLabel.setText(
                    "Camino:\n"
                            + result.getPathAsText()
                            + "\n\nDistancia: "
                            + result.getTotalDistance()
                            + " m"
            );
        });

        card.getChildren().add(originLabel);
        card.getChildren().add(originComboBox);
        card.getChildren().add(destinationLabel);
        card.getChildren().add(destinationComboBox);
        card.getChildren().add(calculateButton);
        card.getChildren().add(resultLabel);

        content.getChildren().add(card);
    }

    /**
     * Agrega un encabezado visual al panel derecho.
     *
     * @param content contenedor principal.
     */
    private void addHeader(VBox content) {
        Label title = new Label("Panel de control");
        title.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 0 0 8 0;"
        );

        content.getChildren().add(title);
    }

    /**
     * Agrega la leyenda visual al panel derecho.
     *
     * Antes esta información se dibujaba encima del grafo.
     * Ahora se coloca en el panel de control para evitar que tape nodos,
     * rutas o aristas en los distintos casos de prueba.
     *
     * @param content contenedor principal.
     */
    private void addLegend(VBox content) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Información relevante"));

        card.getChildren().add(createLegendText("■ Depósito", "#B71C1C"));
        card.getChildren().add(createLegendText("■ Casa / entrega", "#2E7D32"));
        card.getChildren().add(createLegendText("● Intersección", "#264653"));
        card.getChildren().add(createLegendText("━ MST", "#7B1FA2"));
        card.getChildren().add(createLegendText("● Paquete no asignado", "#FBC02D"));
        card.getChildren().add(createLegendText("━ Ruta C01", "#FF0000"));
        card.getChildren().add(createLegendText("━ Ruta C02", "#0000FF"));
        card.getChildren().add(createLegendText("━ Ruta C03", "#008000"));

        content.getChildren().add(card);
    }

    /**
     * Agrega el resumen general del caso.
     *
     * @param content contenedor principal.
     * @param data datos del proyecto.
     */
    private void addGeneralSummary(VBox content, LogisticsData data) {
        Graph graph = data.getGraph();

        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Resumen del caso"));
        card.getChildren().add(createText("Depósito: " + data.getDepotId()));
        card.getChildren().add(createText("Vértices: " + graph.getVertexCount()));
        card.getChildren().add(createText("Paquetes: " + data.getPackages().length));
        card.getChildren().add(createText("Camiones: " + data.getTrucks().length));

        content.getChildren().add(card);
    }

    /**
     * Agrega la información del árbol de expansión mínima.
     *
     * @param content contenedor principal.
     * @param mstResult resultado del MST.
     */
    private void addMSTSummary(VBox content, MSTResult mstResult) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Árbol de expansión mínima"));

        if (mstResult == null) {
            card.getChildren().add(createText("No hay MST calculado."));
            content.getChildren().add(card);
            return;
        }

        card.getChildren().add(createText("Algoritmo mostrado: " + mstResult.getAlgorithmName()));
        card.getChildren().add(createText("Costo total: " + mstResult.getTotalCost() + " m"));
        card.getChildren().add(createText("Aristas del MST: " + mstResult.getEdgeCount()));
        card.getChildren().add(createText("Tiempo: " + mstResult.getExecutionTime() + " ns"));

        content.getChildren().add(card);
    }

    /**
     * Agrega la información de los camiones.
     *
     * @param content contenedor principal.
     * @param trucks camiones del sistema.
     */
    private void addTrucks(VBox content, Truck[] trucks) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Camiones"));

        for (int i = 0; i < trucks.length; i++) {
            Truck truck = trucks[i];

            String text =
                    truck.getId()
                            + " | Carga: " + truck.getCurrentLoad()
                            + " kg / " + truck.getMaxCapacity() + " kg"
                            + " | Ocupación: "
                            + String.format("%.2f", truck.getOccupancyPercentage())
                            + "%";

            card.getChildren().add(createText(text));
        }

        content.getChildren().add(card);
    }

    /**
     * Agrega la comparación de rutas por camión.
     *
     * Para cada camión se muestra:
     * - Distancia con Nearest Neighbor.
     * - Distancia con MST-Based.
     * - Heurística seleccionada.
     * - Ahorro porcentual de MST-Based respecto a Nearest Neighbor.
     *
     * @param content contenedor principal.
     * @param routeComparisons comparaciones calculadas.
     */
    private void addRouteComparisons(
            VBox content,
            RouteComparisonResult[] routeComparisons
    ) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Comparación de heurísticas"));

        if (routeComparisons == null || routeComparisons.length == 0) {
            card.getChildren().add(createText("No hay rutas calculadas."));
            content.getChildren().add(card);
            return;
        }

        for (int i = 0; i < routeComparisons.length; i++) {
            RouteComparisonResult comparison = routeComparisons[i];

            if (comparison == null) {
                continue;
            }

            RouteResult nearestRoute = comparison.getNearestNeighborRoute();
            RouteResult mstRoute = comparison.getMstBasedRoute();
            RouteResult selectedRoute = comparison.getSelectedRoute();

            StringBuilder builder = new StringBuilder();

            builder.append("Camión ")
                    .append(selectedRoute.getTruckId())
                    .append("\nNearest Neighbor: ")
                    .append(nearestRoute.getTotalDistance())
                    .append(" m | ")
                    .append(getTravelTimeText(nearestRoute.getTotalDistance()))
                    .append("\nMST-Based: ")
                    .append(mstRoute.getTotalDistance())
                    .append(" m | ")
                    .append(getTravelTimeText(mstRoute.getTotalDistance()))
                    .append("\nSeleccionada: ")
                    .append(comparison.getSelectedHeuristicName())
                    .append("\nTiempo estimado seleccionado: ")
                    .append(getTravelTimeText(selectedRoute.getTotalDistance()))
                    .append("\nAhorro MST-Based: ")
                    .append(String.format("%.2f", comparison.getMstSavingPercentage()))
                    .append("%");

            builder.append("\nRuta seleccionada: ");
            builder.append(selectedRoute.getDepotId());

            String[] stops = selectedRoute.getOrderedStops();

            for (int j = 0; j < selectedRoute.getStopCount(); j++) {
                builder.append(" -> ").append(stops[j]);
            }

            builder.append(" -> ").append(selectedRoute.getDepotId());

            card.getChildren().add(createRouteText(builder.toString()));
        }

        content.getChildren().add(card);
    }

    /**
     * Agrega la lista de paquetes no asignados.
     *
     * Un paquete puede quedar no asignado por dos razones principales:
     *
     * 1. Destino inalcanzable:
     *    Warshall indica que no existe camino desde el depósito hasta el destino.
     *
     * 2. Capacidad insuficiente:
     *    El destino sí es alcanzable, pero ningún camión pudo cargar el paquete.
     *
     * Esta sección ayuda a defender mejor el requisito de validación de destinos
     * y asignación de paquetes.
     *
     * @param content contenedor principal del panel.
     * @param data datos cargados del proyecto.
     */
    private void addUnassignedPackages(VBox content, LogisticsData data) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Paquetes no asignados"));

        Graph graph = data.getGraph();
        Package[] packages = data.getPackages();
        Truck[] trucks = data.getTrucks();
        String depotId = data.getDepotId();

        /*
         * Se calcula la matriz de alcanzabilidad con Warshall.
         * Esta matriz permite saber si un destino se puede alcanzar desde el depósito.
         */
        boolean[][] reachabilityMatrix = GraphAlgorithms.warshall(graph, depotId);

        int unassignedCount = 0;

        for (int i = 0; i < packages.length; i++) {
            Package currentPackage = packages[i];

            if (!isPackageAssigned(currentPackage, trucks)) {
                String reason = getUnassignedReason(
                        graph,
                        currentPackage,
                        depotId,
                        reachabilityMatrix
                );

                String text =
                        currentPackage.getId()
                                + " | Destino: " + currentPackage.getDestinationVertexId()
                                + " | Peso: " + currentPackage.getWeight() + " kg"
                                + " | Prioridad: " + currentPackage.getPriority()
                                + "\nMotivo: " + reason;

                card.getChildren().add(createWarningText(text));
                unassignedCount++;
            }
        }

        if (unassignedCount == 0) {
            card.getChildren().add(createText("No hay paquetes no asignados."));
        }

        content.getChildren().add(card);
    }
    /**
     * Determina por qué un paquete no fue asignado.
     *
     * @param graph grafo de la ciudad.
     * @param packageToCheck paquete no asignado.
     * @param depotId id del depósito.
     * @param reachabilityMatrix matriz de Warshall.
     * @return motivo textual de no asignación.
     */
    private String getUnassignedReason(
            Graph graph,
            Package packageToCheck,
            String depotId,
            boolean[][] reachabilityMatrix
    ) {
        int depotIndex = graph.getVertexIndex(depotId);
        int destinationIndex = graph.getVertexIndex(packageToCheck.getDestinationVertexId());

        if (depotIndex == -1 || destinationIndex == -1) {
            return "Destino inexistente en el grafo.";
        }

        if (!reachabilityMatrix[depotIndex][destinationIndex]) {
            return "Destino inalcanzable desde el depósito.";
        }

        return "Capacidad insuficiente en la flota.";
    }

    /**
     * Verifica si un paquete fue asignado a algún camión.
     *
     * @param packageToFind paquete que se desea buscar.
     * @param trucks camiones del sistema.
     * @return true si el paquete está asignado.
     */
    private boolean isPackageAssigned(Package packageToFind, Truck[] trucks) {
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
     * Crea una tarjeta visual para una sección.
     *
     * @return VBox con estilo de tarjeta.
     */
    private VBox createCard() {
        VBox card = new VBox(8);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #CFD8DC;" +
                        "-fx-padding: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 2);"
        );

        return card;
    }

    /**
     * Crea un título de sección.
     *
     * @param text texto del título.
     * @return label configurado.
     */
    private Label createSectionTitle(String text) {
        Label label = new Label(text);

        label.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 0 0 6 0;"
        );

        return label;
    }

    /**
     * Crea una etiqueta para la leyenda con color personalizado.
     *
     * @param text texto de la leyenda.
     * @param color color en formato hexadecimal.
     * @return label configurado.
     */
    private Label createLegendText(String text, String color) {
        Label label = new Label(text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + color + ";" +
                        "-fx-padding: 3;"
        );

        return label;
    }

    /**
     * Crea una etiqueta de texto normal.
     *
     * @param text contenido.
     * @return label configurado.
     */
    private Label createText(String text) {
        Label label = new Label(text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 5;" +
                        "-fx-background-color: #F5F5F5;" +
                        "-fx-background-radius: 6;"
        );

        return label;
    }

    /**
     * Crea una etiqueta especial para rutas.
     *
     * @param text contenido de la ruta.
     * @return label configurado.
     */
    private Label createRouteText(String text) {
        Label label = new Label(text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 8;" +
                        "-fx-background-color: #E3F2FD;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #BBDEFB;" +
                        "-fx-border-radius: 8;"
        );

        return label;
    }

    /**
     * Calcula el tiempo estimado de viaje en minutos.
     *
     * Para este proyecto se asume una velocidad promedio de 40 km/h.
     *
     * Conversión:
     * 40 km/h = 40000 metros / 60 minutos
     * 40 km/h = 666.67 metros por minuto aproximadamente.
     *
     * @param distanceMeters distancia total de la ruta en metros.
     * @return tiempo estimado en minutos.
     */
    private double calculateTravelTimeMinutes(int distanceMeters) {
        double metersPerMinute = 40000.0 / 60.0;

        return distanceMeters / metersPerMinute;
    }

    /**
     * Retorna el texto del tiempo estimado de viaje.
     *
     * @param distanceMeters distancia total en metros.
     * @return texto con el tiempo en minutos.
     */
    private String getTravelTimeText(int distanceMeters) {
        double minutes = calculateTravelTimeMinutes(distanceMeters);

        return String.format("%.2f min", minutes);
    }

    /**
     * Crea una etiqueta especial para advertencias o paquetes no asignados.
     *
     * @param text texto que se desea mostrar.
     * @return label configurado.
     */
    private Label createWarningText(String text) {
        Label label = new Label(text);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #4E342E;" +
                        "-fx-padding: 8;" +
                        "-fx-background-color: #FFF3E0;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #FFCC80;" +
                        "-fx-border-radius: 8;"
        );

        return label;
    }
}