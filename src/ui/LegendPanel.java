package ui;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Clase LegendPanel.
 *
 * Este panel se muestra al lado derecho del grafo.
 *
 * A diferencia de InfoPanel, este panel solo muestra la información relevante
 * o leyenda visual del grafo. El panel completo queda disponible desde el botón
 * "Ver panel completo".
 */
public class LegendPanel extends ScrollPane {

    /**
     * Constructor del panel de leyenda.
     */
    public LegendPanel() {
        VBox content = new VBox(14);

        content.setStyle(
                "-fx-padding: 20;" +
                        "-fx-background-color: #ECEFF1;"
        );

        content.getChildren().add(createHeader());
        content.getChildren().add(createLegendCard());

        setContent(content);
        setFitToWidth(true);

        /*
         * Ancho más pequeño que el InfoPanel completo.
         * Así el grafo gana más espacio visual.
         */
        setPrefWidth(285);
        setMinWidth(285);
        setMaxWidth(285);

        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setStyle(
                "-fx-background: #ECEFF1;" +
                        "-fx-background-color: #ECEFF1;" +
                        "-fx-border-color: transparent;"
        );
    }

    /**
     * Crea el título del panel.
     *
     * @return label con el título.
     */
    private Label createHeader() {
        Label title = new Label("Panel de control");

        title.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 0 0 8 0;"
        );

        return title;
    }

    /**
     * Crea la tarjeta con la información relevante.
     *
     * @return tarjeta visual.
     */
    private VBox createLegendCard() {
        VBox card = new VBox(12);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #CFD8DC;" +
                        "-fx-padding: 14;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 8, 0, 0, 2);"
        );

        Label sectionTitle = new Label("Información relevante");

        sectionTitle.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-padding: 0 0 6 0;"
        );

        card.getChildren().add(sectionTitle);

        card.getChildren().add(createLegendText("■ Depósito", "#B71C1C"));
        card.getChildren().add(createLegendText("■ Casa / entrega", "#2E7D32"));
        card.getChildren().add(createLegendText("● Intersección", "#264653"));
        card.getChildren().add(createLegendText("━ MST", "#7B1FA2"));
        card.getChildren().add(createLegendText("● Paquete no asignado", "#FBC02D"));
        card.getChildren().add(createLegendText("━ Ruta C01", "#FF0000"));
        card.getChildren().add(createLegendText("━ Ruta C02", "#0000FF"));
        card.getChildren().add(createLegendText("━ Ruta C03", "#008000"));

        return card;
    }

    /**
     * Crea una línea de leyenda con color personalizado.
     *
     * @param text texto que se muestra.
     * @param color color en hexadecimal.
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
}