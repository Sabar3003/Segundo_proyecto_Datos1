package ui;

import algorithms.GraphAlgorithms;
import algorithms.MSTAlgorithms;
import algorithms.MSTResult;
import graph.Graph;
import io.LogisticsData;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.RouteResult;
import model.Truck;
import planner.RoutePlanner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Clase MainWindow.
 *
 * Esta clase representa la interfaz principal de LogisTEC.
 *
 * Ahora la interfaz tiene dos pantallas:
 * - Pantalla de bienvenida con el título LogisTEC y un botón para visualizar.
 * - Pantalla del grafo con rutas, MST, paquetes y camiones.
 *
 * Esta clase no carga JSON.
 * Los datos ya vienen cargados desde Main mediante JsonLoader.
 */
public class MainWindow extends Application {

    /*
     * JavaFX ejecuta start(Stage stage) sin permitir parámetros directos.
     * Por eso se guardan los datos cargados en esta variable estática
     * antes de lanzar la ventana.
     */
    private static LogisticsData logisticsData;

    /**
     * Lanza la ventana principal de JavaFX.
     *
     * @param data datos cargados desde el archivo JSON.
     */
    public static void launchWindow(LogisticsData data) {
        logisticsData = data;
        launch();
    }

    /**
     * Método principal de JavaFX.
     *
     * Primero muestra la pantalla de bienvenida.
     *
     * @param stage ventana principal.
     */
    @Override
    public void start(Stage stage) {
        if (logisticsData == null) {
            throw new IllegalStateException("No hay datos cargados para mostrar en la interfaz.");
        }

        showWelcomeScene(stage);
    }

    /**
     * Muestra la pantalla inicial de la aplicación con la imagen de fondo integrada.
     * Mantiene únicamente el botón interactivo sobre el diseño.
     *
     * @param stage ventana principal.
     */
    /**
     * Muestra la pantalla inicial de la aplicación con la imagen de fondo integrada.
     * Ubica el botón verde de forma personalizada sobre el cajón del camión.
     *
     * @param stage ventana principal.
     */
    private void showWelcomeScene(Stage stage) {
        // 1. Contenedor raíz para apilar la imagen y los componentes
        StackPane rootContainer = new StackPane();

        try {
            // 2. Cargar la imagen editada desde la ruta física del proyecto
            Image backgroundImage = new Image("file:src/ui/assets/fondopringrafo.png");
            ImageView backgroundView = new ImageView(backgroundImage);

            backgroundView.setPreserveRatio(false);
            backgroundView.fitWidthProperty().bind(rootContainer.widthProperty());
            backgroundView.fitHeightProperty().bind(rootContainer.heightProperty());
            backgroundView.setOpacity(1.0);

            rootContainer.getChildren().add(backgroundView);
            rootContainer.setStyle("-fx-background-color: #263238;");

        } catch (Exception e) {
            System.out.println("Alerta: No se pudo cargar la imagen de fondo.");
            System.out.println("Detalle: " + e.getMessage());
            rootContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #263238, #455A64);");
        }

        // 3. Contenedor exclusivo para el botón (usamos StackPane interno para control total de posición)
        StackPane componentLayout = new StackPane();
        componentLayout.setStyle("-fx-background-color: transparent;");

        // 4. Configuración del botón verde original
        Button visualizeButton = new Button("Visualizar grafo");
        visualizeButton.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #00C853;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 12 30 12 30;" +
                        "-fx-background-radius: 8;"
        );

        /*
         * AJUSTE DE POSICIÓN DINÁMICA (Efecto sticker en el camión)
         * - setTranslateY: Mueve el botón hacia abajo (valores positivos bajan el elemento).
         * - setTranslateX: Mueve el botón a la derecha (valores positivos mueven a la derecha).
         */
        visualizeButton.setTranslateY(150); // Lo baja bastante para meterlo en el cajón blanco
        visualizeButton.setTranslateX(45);  // Lo mueve un poquito a la derecha para centrarlo con el cajón

        /*
         * Cuando se presiona el botón, se cambia a la pantalla del grafo.
         */
        visualizeButton.setOnAction(event -> showGraphScene(stage));

        // 5. Agregamos el botón al layout transparente
        componentLayout.getChildren().add(visualizeButton);

        // 6. Colocamos el layout del botón sobre la capa de la imagen
        rootContainer.getChildren().add(componentLayout);

        Scene scene = new Scene(rootContainer, 1150, 700);

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
        Graph graph = logisticsData.getGraph();
        String depotId = logisticsData.getDepotId();

        /*
         * Floyd-Warshall se usa para calcular la matriz de distancias mínimas.
         * Esta matriz es necesaria para las heurísticas de rutas.
         */
        int[][] distanceMatrix =
                GraphAlgorithms.getFloydWarshallDistanceMatrix(graph);

        /*
         * Se calcula el MST con Prim para dibujarlo en el grafo.
         * Kruskal ya se compara en el reporte de consola.
         */
        MSTResult mstResult = MSTAlgorithms.prim(graph, depotId);

        /*
         * Se calculan las rutas recomendadas para cada camión.
         */
        RouteResult[] recommendedRoutes =
                buildRecommendedRoutes(graph, distanceMatrix, logisticsData.getTrucks(), depotId);

        /*
         * Panel central: dibujo del grafo.
         */
        GraphPane graphPane = new GraphPane(
                graph,
                recommendedRoutes,
                mstResult,
                logisticsData.getPackages(),
                logisticsData.getTrucks()
        );

        /*
         * Panel derecho: información textual.
         */
        InfoPanel infoPanel = new InfoPanel(
                logisticsData,
                recommendedRoutes,
                mstResult,
                graphPane
        );

        BorderPane root = new BorderPane();
        root.setCenter(graphPane);
        root.setRight(infoPanel);

        Scene scene = new Scene(root, 1150, 700);

        stage.setTitle("LogisTEC - Visualización de rutas");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Construye una ruta recomendada para cada camión.
     *
     * Para cada camión se calculan dos rutas:
     * - Nearest Neighbor.
     * - MST-Based.
     *
     * Luego se selecciona la ruta con menor distancia.
     *
     * @param graph grafo de la ciudad.
     * @param distanceMatrix matriz de distancias mínimas.
     * @param trucks camiones con paquetes asignados.
     * @param depotId id del depósito.
     * @return arreglo con la mejor ruta de cada camión.
     */
    private RouteResult[] buildRecommendedRoutes(
            Graph graph,
            int[][] distanceMatrix,
            Truck[] trucks,
            String depotId
    ) {
        RouteResult[] recommendedRoutes = new RouteResult[trucks.length];

        for (int i = 0; i < trucks.length; i++) {
            RouteResult nearestRoute =
                    RoutePlanner.nearestNeighbor(graph, distanceMatrix, trucks[i], depotId);

            RouteResult mstRoute =
                    RoutePlanner.mstBasedRoute(graph, distanceMatrix, trucks[i], depotId);

            if (nearestRoute.getTotalDistance() <= mstRoute.getTotalDistance()) {
                recommendedRoutes[i] = nearestRoute;
            } else {
                recommendedRoutes[i] = mstRoute;
            }
        }

        return recommendedRoutes;
    }
}