package ui;

import algorithms.MSTResult;
import graph.Graph;
import io.LogisticsData;
import model.Package;
import model.RouteResult;
import model.Truck;

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
    public InfoPanel(LogisticsData data, RouteResult[] routes, MSTResult mstResult) {

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