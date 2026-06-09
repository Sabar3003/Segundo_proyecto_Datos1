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
            GraphPane graphPane
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
                "-fx-padding: 20;" +
                        "-fx-background-color: #ECEFF1;"
        );

        addHeader(content);
        addGeneralSummary(content, data);
        addShortestPathQuery(content, data, graphPane);
        addMSTSummary(content, mstResult);
        addTrucks(content, data.getTrucks());
        addRoutes(content, routes);
        addPendingPackages(content, data.getPackages(), data.getTrucks());

        /*
         * Configuración del ScrollPane.
         * Esto permite bajar cuando hay mucha información.
         */
        setContent(content);
        setFitToWidth(true);
        setPrefWidth(400);

        /*
         * Quitamos bordes innecesarios y mantenemos el fondo del mismo color.
         */
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
     * Agrega las rutas recomendadas.
     *
     * @param content contenedor principal.
     * @param routes rutas calculadas.
     */
    private void addRoutes(VBox content, RouteResult[] routes) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Rutas recomendadas"));

        if (routes == null || routes.length == 0) {
            card.getChildren().add(createText("No hay rutas calculadas."));
            content.getChildren().add(card);
            return;
        }

        for (int i = 0; i < routes.length; i++) {
            RouteResult route = routes[i];

            if (route == null) {
                continue;
            }

            StringBuilder builder = new StringBuilder();

            builder.append("Camión ")
                    .append(route.getTruckId())
                    .append("\nHeurística: ")
                    .append(route.getHeuristicName())
                    .append("\nDistancia: ")
                    .append(route.getTotalDistance())
                    .append(" m");

            builder.append("\nOrden: ");
            builder.append(route.getDepotId());

            String[] stops = route.getOrderedStops();

            for (int j = 0; j < route.getStopCount(); j++) {
                builder.append(" -> ").append(stops[j]);
            }

            builder.append(" -> ").append(route.getDepotId());

            card.getChildren().add(createRouteText(builder.toString()));
        }

        content.getChildren().add(card);
    }

    /**
     * Agrega la lista de paquetes pendientes.
     *
     * Un paquete pendiente es aquel que no fue asignado a ningún camión.
     *
     * @param content contenedor principal.
     * @param packages paquetes cargados.
     * @param trucks camiones con paquetes asignados.
     */
    private void addPendingPackages(VBox content, Package[] packages, Truck[] trucks) {
        VBox card = createCard();

        card.getChildren().add(createSectionTitle("Paquetes pendientes"));

        int pendingCount = 0;

        for (int i = 0; i < packages.length; i++) {
            Package currentPackage = packages[i];

            if (!isPackageAssigned(currentPackage, trucks)) {
                card.getChildren().add(createText(currentPackage.toString()));
                pendingCount++;
            }
        }

        if (pendingCount == 0) {
            card.getChildren().add(createText("No hay paquetes pendientes."));
        }

        content.getChildren().add(card);
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
}