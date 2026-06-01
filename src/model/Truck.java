package model;

/**
 * Clase Truck.
 *
 * Representa un camión de LogísTEC.
 *
 * Según el proyecto, cada camión tiene:
 * - Un identificador.
 * - Una capacidad máxima de carga en kg.
 *
 * Esta clase también guarda:
 * - La carga actual del camión.
 * - Los paquetes asignados al camión.
 *
 * La clase no calcula rutas.
 * Solo administra la información de carga y paquetes del camión.
 */

public class Truck {

    // Identificador del camión, por ejemplo: C01, C02, C03.
    private String id;

    // Capacidad máxima del camión en kilogramos.
    private int maxCapacity;

    // Carga actual del camión en kilogramos.
    private int currentLoad;

    // Arreglo de paquetes asignados al camión.
    private Package[] assignedPackages;

    // Cantidad real de paquetes asignados.
    private int assignedPackageCount;

    /**
     * Constructor de Truck.
     *
     * @param id identificador del camión.
     * @param maxCapacity capacidad máxima del camión en kilogramos.
     * @param maxPackages cantidad máxima de paquetes que se podrían asignar.
     */

    public Truck(String id, int maxCapacity, int maxPackages) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.currentLoad = 0;
        this.assignedPackages = new Package[maxPackages];
        this.assignedPackageCount = 0;
    }

    /**
     * Retorna el identificador del camión.
     *
     * @return id del camión.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna la capacidad máxima del camión.
     *
     * @return capacidad máxima en kilogramos.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Retorna la carga actual del camión.
     *
     * @return carga actual en kilogramos.
     */
    public int getCurrentLoad() {
        return currentLoad;
    }

    /**
     * Calcula la capacidad libre del camión.
     *
     * @return capacidad disponible en kilogramos.
     */
    public int getRemainingCapacity() {
        return maxCapacity - currentLoad;
    }

    /**
     * Retorna los paquetes asignados al camión.
     *
     * Importante:
     * El arreglo puede tener posiciones null al final.
     * Para recorrerlo correctamente se debe usar assignedPackageCount.
     *
     * @return arreglo de paquetes asignados.
     */
    public Package[] getAssignedPackages() {
        return assignedPackages;
    }

    /**
     * Retorna cuántos paquetes tiene asignados realmente el camión.
     *
     * @return cantidad de paquetes asignados.
     */
    public int getAssignedPackageCount() {
        return assignedPackageCount;
    }

    /**
     * Verifica si el camión puede cargar un paquete según su peso.
     *
     * @param packageToCheck paquete que se desea revisar.
     * @return true si el paquete cabe en la capacidad restante.
     */
    public boolean canCarry(Package packageToCheck) {
        return packageToCheck.getWeight() <= getRemainingCapacity();
    }

    /**
     * Intenta asignar un paquete al camión.
     *
     * Primero valida dos cosas:
     * - Que todavía haya espacio en el arreglo de paquetes.
     * - Que el peso del paquete no supere la capacidad restante.
     *
     * Si ambas condiciones se cumplen, el paquete se guarda y se suma su peso
     * a la carga actual del camión.
     *
     * @param packageToAdd paquete que se desea asignar.
     * @return true si se asignó correctamente, false si no se pudo.
     */
    public boolean addPackage(Package packageToAdd) {

        // Si ya no hay espacio en el arreglo, no se puede guardar otro paquete.
        if (assignedPackageCount >= assignedPackages.length) {
            return false;
        }

        // Si el paquete no cabe por peso, no se puede asignar.
        if (!canCarry(packageToAdd)) {
            return false;
        }

        //Si sí
        // Guardamos el paquete en la siguiente posición libre.
        assignedPackages[assignedPackageCount] = packageToAdd;

        // Sumamos el peso del paquete a la carga actual.
        currentLoad += packageToAdd.getWeight();

        // Aumentamos la cantidad de paquetes asignados.
        assignedPackageCount++;

        return true;
    }

    /**
     * Calcula el porcentaje de ocupación del camión.
     *
     * Fórmula:
     * carga actual * 100 / capacidad máxima
     *
     * @return porcentaje de ocupación.
     */
    public double getOccupancyPercentage() {
        if (maxCapacity == 0) {
            return 0;
        }

        return (currentLoad * 100.0) / maxCapacity;
    }

    /**
     * Imprime en consola los paquetes asignados al camión.
     *
     * Este método sirve para pruebas en Main y para revisar la asignación.
     */
    public void printAssignedPackages() {
        System.out.println("\nCamión " + id);
        System.out.println("Carga: " + currentLoad + "kg / " + maxCapacity + "kg");
        System.out.println("Ocupación: " + getOccupancyPercentage() + "%");
        System.out.println("Paquetes asignados:");

        if (assignedPackageCount == 0) {
            System.out.println("No tiene paquetes asignados.");
            return;
        }

        for (int i = 0; i < assignedPackageCount; i++) {
            System.out.println(assignedPackages[i]);
        }
    }

    /**
     * Representación textual básica del camión.
     *
     * @return texto con id y carga.
     */
    @Override
    public String toString() {
        return id + " -> carga: " + currentLoad + "kg / " + maxCapacity + "kg";
    }
}