package app;

import io.JsonLoader;
import io.LogisticsData;
import io.ReportGenerator;
import ui.MainWindow;

/**
 * Clase principal del proyecto LogisTEC.
 *
 * Esta versión carga el caso de prueba desde JSON, genera el reporte en consola
 * y abre la interfaz gráfica JavaFX.
 */
public class Main {

    public static void main(String[] args) {

        try {
            /*
             * Ruta relativa del archivo JSON.
             */
            String filePath = "data/caso_minimo.json";

            /*
             * Cargamos los datos principales del caso.
             */
            LogisticsData data = JsonLoader.load(filePath);

            /*
             * Generamos el reporte en consola.
             *
             * Este reporte también ejecuta la asignación de paquetes a camiones.
             */
            ReportGenerator.generateFullReport(data);

            /*
             * Ejecutamos la consulta directa de Dijkstra origen-destino para el reporte.
             */
            ReportGenerator.mostrarConsultaDijkstra(data.getGraph(), "V01", "V24");
            MainWindow.launchWindow(data);

        } catch (Exception exception) {
            System.out.println("Error al ejecutar LogisTEC:");
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
}