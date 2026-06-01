package planner;

import model.AssignmentResult;
import model.Package;
import model.Truck;

/**
 * Clase PackageAssigner.
 *
 * Esta clase se encarga de asignar paquetes a camiones.
 *
 * Según el proyecto, la heurística mínima exigida es:
 *
 * 1. Ordenar paquetes por prioridad ascendente.
 *    prioridad 1 primero, luego prioridad 2 y luego prioridad 3.
 *
 * 2. Si dos paquetes tienen la misma prioridad,
 *    se ordenan por peso descendente.
 *
 * 3. Para cada paquete, asignarlo al camión con mayor capacidad libre
 *    que pueda cargarlo.
 *
 * 4. Si ningún camión puede llevarlo, marcarlo como rechazado.
 *
 * Esta clase no calcula rutas.
 * Solo decide qué paquetes van en cada camión.
 */
public class PackageAssigner {

    /**
     * Asigna paquetes a camiones usando la heurística solicitada.
     *
     * Este método recibe los paquetes y camiones, ordena los paquetes
     * según prioridad/peso y luego intenta asignarlos uno por uno.
     *
     * @param packages paquetes que se desean asignar.
     * @param trucks camiones disponibles.
     * @return resultado de la asignación.
     */
    public static AssignmentResult assignPackages(Package[] packages, Truck[] trucks) {

        // Creamos el resultado de asignación.
        // Se pasa el arreglo de camiones porque ahí se irán guardando los paquetes asignados.
       
        // El máximo de rechazados posible es packages.length
        AssignmentResult result = new AssignmentResult(trucks, packages.length);

        // Ordenamos los paquetes antes de asignarlos.
        // Esto modifica el arreglo recibido, dejándolo en el orden que exige el proyecto.
        sortPackagesByPriorityAndWeight(packages);

        // Recorremos los paquetes ya ordenados.
        for (int i = 0; i < packages.length; i++) {

            Package currentPackage = packages[i];

            // Buscamos el mejor camión para este paquete.
            Truck selectedTruck = findTruckWithMostFreeCapacity(currentPackage, trucks);

            // Si encontramos un camión válido, le asignamos el paquete.
            if (selectedTruck != null) {
                selectedTruck.addPackage(currentPackage);
            } else {
                // Si ningún camión puede llevarlo, lo marcamos como rechazado.
                result.addRejectedPackage(currentPackage);
            }
        }

        return result;
    }

    /**
     * Ordena los paquetes según la regla del proyecto:
     *
     * 1. Prioridad ascendente:
     *    prioridad 1 antes que 2, prioridad 2 antes que 3.
     *
     * 2. Si tienen la misma prioridad:
     *    peso descendente.
     *
     * Se usa ordenamiento burbuja para mantenerlo fácil de entender
     *
     * @param packages arreglo de paquetes a ordenar.
     */
    private static void sortPackagesByPriorityAndWeight(Package[] packages) {

        // Recorremos el arreglo varias veces.
        for (int i = 0; i < packages.length - 1; i++) {

            // Comparamos pares vecinos.
            for (int j = 0; j < packages.length - 1 - i; j++) {

                // Si el paquete actual debe ir después del siguiente,
                // entonces los intercambiamos.
                if (shouldSwap(packages[j], packages[j + 1])) {

                    Package temp = packages[j];
                    packages[j] = packages[j + 1];
                    packages[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Decide si dos paquetes deben intercambiarse durante el ordenamiento.
     *
     * Retorna true cuando first debe ir después de second.
     *
     * Casos:
     * - Si first tiene prioridad mayor numéricamente que second,
     *   first debe ir después.
     *
     *   Ejemplo:
     *   first prioridad 3, second prioridad 1.
     *   Como 1 es más urgente, se intercambian.
     *
     * - Si tienen la misma prioridad, pero first pesa menos que second,
     *   first debe ir después porque el proyecto pide peso descendente.
     *
     * @param first primer paquete comparado.
     * @param second segundo paquete comparado.
     * @return true si se deben intercambiar.
     */
    private static boolean shouldSwap(Package first, Package second) {

        // Si la prioridad del primero es mayor, significa que es menos urgente.
        // Por ejemplo, prioridad 3 debe ir después de prioridad 1.
        if (first.getPriority() > second.getPriority()) {
            return true;
        }

        // Si tienen la misma prioridad, va primero el más pesado.
        if (first.getPriority() == second.getPriority()
                && first.getWeight() < second.getWeight()) {
            return true;
        }

        return false;
    }

    /**
     * Busca el camión con mayor capacidad libre que pueda cargar el paquete.
     *
     * Según el proyecto, para cada paquete se debe elegir el camión con
     * mayor capacidad libre entre los que sí puedan alojarlo.
     *
     * @param packageToAssign paquete que se desea asignar.
     * @param trucks camiones disponibles.
     * @return camión seleccionado o null si ninguno puede cargarlo.
     */
    private static Truck findTruckWithMostFreeCapacity(Package packageToAssign, Truck[] trucks) {

        // Al inicio no hemos encontrado ningún camión válido.
        Truck selectedTruck = null;

        // Guarda la mayor capacidad libre encontrada hasta el momento.
        int maxRemainingCapacity = -1;

        // Revisamos todos los camiones.
        for (int i = 0; i < trucks.length; i++) {

            Truck currentTruck = trucks[i];

            // Si el camión puede cargar el paquete, entonces es candidato.
            if (currentTruck.canCarry(packageToAssign)) {

                int remainingCapacity = currentTruck.getRemainingCapacity();

                // Si este camión tiene más capacidad libre que el mejor actual,
                // pasa a ser el seleccionado.
                if (remainingCapacity > maxRemainingCapacity) {
                    maxRemainingCapacity = remainingCapacity;
                    selectedTruck = currentTruck;
                }
            }
        }

        return selectedTruck;
    }

    /**
     * Método temporal de prueba.
     *
     * Imprime los paquetes en el orden actual del arreglo.
     * Sirve para comprobar que el ordenamiento por prioridad y peso funciona.
     *
     * @param packages arreglo de paquetes.
     */
    public static void printPackagesForTest(Package[] packages) {
        System.out.println("\n--- PAQUETES ---");

        for (int i = 0; i < packages.length; i++) {
            System.out.println(packages[i]);
        }
    }
}