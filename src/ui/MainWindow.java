package ui;

import algorithms.GraphAlgorithms;
import algorithms.MSTAlgorithms;
import algorithms.MSTResult;
import graph.Graph;
import io.JsonLoader;
import io.LogisticsData;
import io.ReportGenerator;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.RouteComparisonResult;
import model.RouteResult;
import model.Truck;
import planner.RoutePlanner;

import java.io.File;

/**
 * Clase MainWindow.
 *
 * Esta clase controla la interfaz principal de LogisTEC.
 *
 * Funciones principales:
 * - Muestra la pantalla inicial con la imagen del camión.
 * - Permite seleccionar un caso JSON del proyecto.
 * - Permite cargar un caso JSON externo.
 * - Valida que se seleccione un caso antes de visualizar.
 * - Carga el JSON seleccionado.
 * - Genera el reporte en consola.
 * - Muestra el grafo con rutas, MST, Dijkstra visual y paquetes.
 * - Permite volver a la pantalla principal para probar otro caso.
 */
public class MainWindow extends Application {

    /*
     * Datos cargados desde el JSON seleccionado.
     */
    private static LogisticsData logisticsData;

    /*
     * Ruta del archivo JSON seleccionado por el usuario.
     */
    private String selectedFilePath;

    /**
     * Lanza la ventana principal de JavaFX.
     */
    public static void launchWindow() {
        launch();
    }

    /**
     * Método inicial de JavaFX.
     *
     * @param stage ventana principal.
     */
    @Override
    public void start(Stage stage) {
        showWelcomeScene(stage);
    }

    /**
     * Muestra la pantalla inicial de la aplicación.
     *
     * Esta pantalla usa la imagen del camión como fondo y coloca
     * los controles dentro del cajón blanco del camión.
     *
     * @param stage ventana principal.
     */
    private void showWelcomeScene(Stage stage) {
        /*
         * Reiniciamos selección para que al volver al inicio no quede
         * seleccionado un archivo anterior por accidente.
         */
        selectedFilePath = null;

        /*
         * Contenedor raíz.
         * StackPane permite poner la imagen de fondo y encima los controles.
         */
        StackPane rootContainer = new StackPane();

        try {
            /*
             * Carga de la imagen de fondo.
             *
             * Importante:
             * La imagen debe estar en:
             * src/ui/assets/fondopringrafo.png
             */
            Image backgroundImage =
                    new Image("file:src/ui/assets/fondopringrafo.png");

            ImageView backgroundView =
                    new ImageView(backgroundImage);

            backgroundView.setPreserveRatio(false);
            backgroundView.fitWidthProperty().bind(rootContainer.widthProperty());
            backgroundView.fitHeightProperty().bind(rootContainer.heightProperty());

            rootContainer.getChildren().add(backgroundView);
            rootContainer.setStyle("-fx-background-color: #263238;");

        } catch (Exception exception) {
            /*
             * Fondo alternativo si la imagen no se puede cargar.
             */
            System.out.println("Alerta: No se pudo cargar la imagen de fondo.");
            System.out.println("Detalle: " + exception.getMessage());

            rootContainer.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #263238, #455A64);"
            );
        }

        /*
         * Contenedor de controles.
         * Este VBox se coloca encima del cajón blanco del camión.
         */
        VBox controlsBox = new VBox(7);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setMaxWidth(420);

        /*
         * Estos valores colocan el formulario aproximadamente dentro
         * del cajón blanco del camión.
         *
         * Si queda un poco arriba/abajo en tu pantalla, solo ajustamos:
         * - setTranslateY
         * - setTranslateX
         */
        controlsBox.setTranslateY(145);
        controlsBox.setTranslateX(70);

        Label instructionLabel =
                new Label("Seleccione el caso a visualizar");

        instructionLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;"
        );

        ComboBox<String> caseComboBox =
                new ComboBox<>();

        caseComboBox.setPromptText("Seleccionar caso JSON");
        caseComboBox.setPrefWidth(300);
        caseComboBox.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-background-radius: 8;"
        );

        /*
         * Casos disponibles dentro del proyecto.
         */
        caseComboBox.getItems().add("Caso pequeño");
        caseComboBox.getItems().add("Caso medio");
        caseComboBox.getItems().add("Caso mínimo obligatorio");
        caseComboBox.getItems().add("Caso con paquetes inalcanzables");

        Label messageLabel =
                new Label("");

        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(390);
        messageLabel.setAlignment(Pos.CENTER);

        messageLabel.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #B71C1C;" +
                        "-fx-font-weight: bold;"
        );

        /*
         * Cuando el usuario selecciona un caso de la lista,
         * se guarda la ruta del archivo JSON correspondiente.
         */
        caseComboBox.setOnAction(event -> {
            String selectedCase = caseComboBox.getValue();

            if ("Caso pequeño".equals(selectedCase)) {
                selectedFilePath = "data/caso_pequeno.json";
                setSuccessMessage(messageLabel, "Caso pequeño seleccionado.");
            } else if ("Caso medio".equals(selectedCase)) {
                selectedFilePath = "data/caso_medio.json";
                setSuccessMessage(messageLabel, "Caso medio seleccionado.");
            } else if ("Caso mínimo obligatorio".equals(selectedCase)) {
                selectedFilePath = "data/caso_minimo.json";
                setSuccessMessage(messageLabel, "Caso mínimo obligatorio seleccionado.");
            } else if ("Caso con paquetes inalcanzables".equals(selectedCase)) {
                selectedFilePath = "data/caso_inaccesible.json";
                setSuccessMessage(messageLabel, "Caso con paquetes inalcanzables seleccionado.");
            }
        });

        Button externalCaseButton =
                new Button("Cargar caso JSON externo");

        externalCaseButton.setPrefWidth(230);
        externalCaseButton.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #455A64;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 7 14 7 14;"
        );

        /*
         * Permite cargar un archivo JSON externo desde la computadora.
         */
        externalCaseButton.setOnAction(event -> {
            FileChooser fileChooser =
                    new FileChooser();

            fileChooser.setTitle("Seleccionar caso JSON");

            FileChooser.ExtensionFilter jsonFilter =
                    new FileChooser.ExtensionFilter(
                            "Archivos JSON (*.json)",
                            "*.json"
                    );

            fileChooser.getExtensionFilters().add(jsonFilter);

            File selectedFile =
                    fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                selectedFilePath =
                        selectedFile.getAbsolutePath();

                caseComboBox.setValue(
                        "Caso externo: " + selectedFile.getName()
                );

                setSuccessMessage(
                        messageLabel,
                        "Caso externo seleccionado correctamente."
                );
            }
        });

        Button visualizeButton =
                new Button("Visualizar grafo");

        visualizeButton.setPrefWidth(230);
        visualizeButton.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #00C853;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18 8 18;"
        );

        /*
         * Botón principal.
         *
         * Si no hay caso seleccionado, muestra un mensaje.
         * Si sí hay caso seleccionado, carga el JSON, genera el reporte
         * y abre la pantalla del grafo.
         */
        visualizeButton.setOnAction(event -> {
            if (selectedFilePath == null || selectedFilePath.isEmpty()) {
                setErrorMessage(
                        messageLabel,
                        "Debe seleccionar un caso antes de visualizar el grafo."
                );
                return;
            }

            try {
                /*
                 * Carga del archivo JSON seleccionado.
                 */
                logisticsData =
                        JsonLoader.load(selectedFilePath);

                /*
                 * Este orden es importante:
                 * Primero se genera el reporte porque ahí se asignan
                 * los paquetes a los camiones.
                 *
                 * Luego se abre la interfaz gráfica del grafo.
                 */
                ReportGenerator.generateFullReport(logisticsData);

                showGraphScene(stage);

            } catch (Exception exception) {
                showErrorAlert(
                        "Error al cargar el caso",
                        "No se pudo cargar el archivo JSON seleccionado.\n\n"
                                + exception.getMessage()
                );

                exception.printStackTrace();
            }
        });

        controlsBox.getChildren().add(instructionLabel);
        controlsBox.getChildren().add(caseComboBox);
        controlsBox.getChildren().add(externalCaseButton);
        controlsBox.getChildren().add(visualizeButton);
        controlsBox.getChildren().add(messageLabel);

        rootContainer.getChildren().add(controlsBox);

        Scene scene =
                new Scene(rootContainer, 1150, 700);

        stage.setTitle("LogisTEC");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Muestra la pantalla donde se visualiza el grafo, rutas, MST y paquetes.
     *
     * @param stage ventana principal.
     */
    private void showGraphScene(Stage stage) {
        Graph graph =
                logisticsData.getGraph();

        String depotId =
                logisticsData.getDepotId();

        /*
         * Floyd-Warshall se usa para calcular la matriz de distancias mínimas.
         * Esta matriz es necesaria para las heurísticas de rutas.
         */
        int[][] distanceMatrix =
                GraphAlgorithms.getFloydWarshallDistanceMatrix(graph);

        /*
         * Se calcula el MST con Prim para dibujarlo en el grafo.
         */
        MSTResult mstResult =
                MSTAlgorithms.prim(graph, depotId);

        /*
         * Se calculan las rutas con ambas heurísticas.
         */
        RouteComparisonResult[] routeComparisons =
                buildRouteComparisons(
                        graph,
                        distanceMatrix,
                        logisticsData.getTrucks(),
                        depotId
                );

        /*
         * De las comparaciones se obtiene la ruta recomendada
         * para dibujarla en el mapa.
         */
        RouteResult[] recommendedRoutes =
                getRecommendedRoutes(routeComparisons);

        /*
         * Panel central: dibujo del grafo.
         */
        GraphPane graphPane =
                new GraphPane(
                        graph,
                        recommendedRoutes,
                        mstResult,
                        logisticsData.getPackages(),
                        logisticsData.getTrucks()
                );

        /*
         * Panel derecho: información textual.
         */
        LegendPanel legendPanel =
                new LegendPanel();

        /*
         * Botón para volver a la pantalla principal.
         */
        Button backButton =
                new Button("Volver al inicio");

        backButton.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #1565C0;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18 8 18;"
        );

        backButton.setOnAction(event -> {
            /*
             * Se limpia la referencia del caso actual.
             * Luego se muestra nuevamente la pantalla inicial.
             */
            logisticsData = null;
            showWelcomeScene(stage);
        });

        Button fullPanelButton =
                new Button("Ver panel completo");

        fullPanelButton.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #2E7D32;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8 18 8 18;"
        );

        fullPanelButton.setOnAction(event -> {
            showFullInfoWindow(
                    logisticsData,
                    recommendedRoutes,
                    mstResult,
                    graphPane,
                    routeComparisons
            );
        });

        Label graphTitle =
                new Label("Visualización del caso seleccionado");

        graphTitle.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;"
        );

        HBox topBar =
                new HBox(20);

        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
                "-fx-background-color: #ECEFF1;" +
                        "-fx-padding: 10;" +
                        "-fx-border-color: #CFD8DC;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        topBar.getChildren().add(backButton);
        topBar.getChildren().add(fullPanelButton);
        topBar.getChildren().add(graphTitle);

        BorderPane root =
                new BorderPane();

        root.setTop(topBar);
        root.setCenter(graphPane);
        root.setRight(legendPanel);

        Scene scene =
                new Scene(root, 1150, 700);

        stage.setTitle("LogisTEC - Visualización de rutas");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Construye la comparación de rutas para cada camión.
     *
     * Para cada camión se calculan dos rutas:
     * - Nearest Neighbor.
     * - MST-Based.
     *
     * Luego cada comparación decide cuál ruta es mejor.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param trucks camiones con paquetes asignados.
     * @param depotId id del depósito.
     * @return arreglo con comparaciones de rutas.
     */
    private RouteComparisonResult[] buildRouteComparisons(
            Graph graph,
            int[][] distanceMatrix,
            Truck[] trucks,
            String depotId
    ) {
        RouteComparisonResult[] comparisons =
                new RouteComparisonResult[trucks.length];

        for (int i = 0; i < trucks.length; i++) {
            RouteResult nearestRoute =
                    RoutePlanner.nearestNeighbor(
                            graph,
                            distanceMatrix,
                            trucks[i],
                            depotId
                    );

            RouteResult mstRoute =
                    RoutePlanner.mstBasedRoute(
                            graph,
                            distanceMatrix,
                            trucks[i],
                            depotId
                    );

            comparisons[i] =
                    new RouteComparisonResult(
                            nearestRoute,
                            mstRoute
                    );
        }

        return comparisons;
    }

    /**
     * Extrae las rutas recomendadas desde las comparaciones.
     *
     * Este arreglo se usa para dibujar en el mapa solamente la mejor ruta
     * de cada camión.
     *
     * @param comparisons comparaciones de rutas.
     * @return arreglo con rutas seleccionadas.
     */
    private RouteResult[] getRecommendedRoutes(
            RouteComparisonResult[] comparisons
    ) {
        RouteResult[] recommendedRoutes =
                new RouteResult[comparisons.length];

        for (int i = 0; i < comparisons.length; i++) {
            recommendedRoutes[i] =
                    comparisons[i].getSelectedRoute();
        }

        return recommendedRoutes;
    }

    /**
     * Muestra un mensaje de éxito en la pantalla inicial.
     *
     * @param label label donde se muestra el mensaje.
     * @param message mensaje a mostrar.
     */
    private void setSuccessMessage(Label label, String message) {
        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #1B5E20;" +
                        "-fx-font-weight: bold;"
        );

        label.setText(message);
    }

    /**
     * Muestra un mensaje de error en la pantalla inicial.
     *
     * @param label label donde se muestra el mensaje.
     * @param message mensaje a mostrar.
     */
    private void setErrorMessage(Label label, String message) {
        label.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #B71C1C;" +
                        "-fx-font-weight: bold;"
        );

        label.setText(message);
    }

    /**
     * Muestra una alerta de error.
     *
     * @param title título de la alerta.
     * @param message mensaje mostrado.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert =
                new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Muestra una ventana adicional con toda la información del panel de control.
     *
     * Esta ventana permite revisar el reporte completo sin depender del espacio
     * reducido del panel derecho.
     *
     * @param data datos cargados desde JSON.
     * @param recommendedRoutes rutas recomendadas.
     * @param mstResult resultado del MST.
     * @param graphPane panel del grafo.
     * @param routeComparisons comparación de heurísticas por camión.
     */
    private void showFullInfoWindow(
            LogisticsData data,
            RouteResult[] recommendedRoutes,
            MSTResult mstResult,
            GraphPane graphPane,
            RouteComparisonResult[] routeComparisons
    ) {
        InfoPanel fullInfoPanel =
                new InfoPanel(
                        data,
                        recommendedRoutes,
                        mstResult,
                        graphPane,
                        routeComparisons
                );

        /*
         * Como esta ventana es más grande, le damos más espacio al panel.
         */
        fullInfoPanel.setPrefWidth(620);
        fullInfoPanel.setMinWidth(620);
        fullInfoPanel.setMaxWidth(620);
        fullInfoPanel.setPrefHeight(780);

        Stage infoStage =
                new Stage();

        Scene scene =
                new Scene(fullInfoPanel, 620, 780);

        infoStage.setTitle("LogisTEC - Panel de control completo");
        infoStage.setScene(scene);
        infoStage.show();
    }
}