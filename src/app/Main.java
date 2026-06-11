package app;

import ui.MainWindow;

/**
 * Clase principal del proyecto LogisTEC.
 *
 * Esta clase solo inicia la interfaz gráfica.
 * La selección y carga del caso JSON se realiza desde la pantalla inicial.
 */
public class Main {

    /**
     * Método principal del programa.
     *
     * @param args argumentos de ejecución.
     */
    public static void main(String[] args) {
        MainWindow.launchWindow();
    }
}