package model;

/**
 * Clase AssignmentResult.
 *
 * Guarda el resultado final de la asignación de paquetes a camiones.
 *
 * Esta clase no asigna paquetes.
 * Solo guarda:
 * - Los camiones después de recibir paquetes.
 * - Los paquetes rechazados.
 *
 * Los paquetes asignados quedan guardados dentro de cada Truck.
 * Los paquetes rechazados quedan guardados en esta clase.
 */
public class AssignmentResult {

    // Camiones usados en la asignación.
    private Truck[] trucks;

    // Paquetes que no pudieron asignarse a ningún camión.
    private Package[] rejectedPackages;

    // Cantidad real de paquetes rechazados.
    private int rejectedPackageCount;

    /**
     * Constructor de AssignmentResult.
     *
     * @param trucks camiones usados en la asignación.
     * @param maxRejectedPackages cantidad máxima posible de paquetes rechazados.
     */
    
    public AssignmentResult(Truck[] trucks, int maxRejectedPackages) {
        this.trucks = trucks;
        this.rejectedPackages = new Package[maxRejectedPackages];
        this.rejectedPackageCount = 0;
    }

    /**
     * Retorna los camiones con sus paquetes ya asignados.
     *
     * @return arreglo de camiones.
     */
    public Truck[] getTrucks() {
        return trucks;
    }

    /**
     * Retorna los paquetes rechazados.
     *
     * Importante:
     * El arreglo puede tener posiciones null al final.
     * Para recorrerlo correctamente se debe usar rejectedPackageCount.
     *
     * @return arreglo de paquetes rechazados.
     */
    public Package[] getRejectedPackages() {
        return rejectedPackages;
    }

    /**
     * Retorna cuántos paquetes fueron rechazados realmente.
     *
     * @return cantidad de paquetes rechazados.
     */
    public int getRejectedPackageCount() {
        return rejectedPackageCount;
    }

    /**
     * Agrega un paquete a la lista de rechazados.
     *
     * Esto ocurre cuando ningún camión tiene capacidad suficiente
     * para cargar el paquete.
     *
     * @param rejectedPackage paquete rechazado.
     * @return true si se agregó correctamente, false si no hay espacio.
     */
    public boolean addRejectedPackage(Package rejectedPackage) {

        // Si el arreglo de rechazados está lleno, no se puede agregar.
        if (rejectedPackageCount >= rejectedPackages.length) {
            return false;
        }

        // Guardamos el paquete rechazado en la siguiente posición libre.
        rejectedPackages[rejectedPackageCount] = rejectedPackage;

        // Aumentamos la cantidad de rechazados.
        rejectedPackageCount++;

        return true;
    }

    /**
     * Imprime el resultado de la asignación en consola.
     *
     * Este método sirve para pruebas en Main.
     * Más adelante, la interfaz puede usar los getters para mostrar la información.
     */
    public void printReport() {
        System.out.println("\n=================================");
        System.out.println("   RESULTADO DE ASIGNACIÓN");
        System.out.println("=================================");

        System.out.println("\n--- CAMIONES ---");

        for (int i = 0; i < trucks.length; i++) {
            trucks[i].printAssignedPackages();
        }

        System.out.println("\n--- PAQUETES RECHAZADOS ---");

        if (rejectedPackageCount == 0) {
            System.out.println("No hubo paquetes rechazados.");
            return;
        }

        for (int i = 0; i < rejectedPackageCount; i++) {
            System.out.println(rejectedPackages[i]);
        }
    }
}