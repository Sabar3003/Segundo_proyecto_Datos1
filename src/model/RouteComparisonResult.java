package model;

/**
 * Clase RouteComparisonResult.
 *
 * Esta clase guarda la comparación entre las dos heurísticas de ruteo:
 * - Nearest Neighbor.
 * - MST-Based.
 *
 * También guarda cuál de las dos rutas fue seleccionada como recomendada.
 *
 * Esta clase no calcula rutas.
 * Solo almacena resultados ya calculados por RoutePlanner.
 */
public class RouteComparisonResult {

    // Ruta calculada con Nearest Neighbor.
    private RouteResult nearestNeighborRoute;

    // Ruta calculada con MST-Based.
    private RouteResult mstBasedRoute;

    // Ruta seleccionada como mejor opción.
    private RouteResult selectedRoute;

    // Porcentaje de ahorro de MST-Based respecto a Nearest Neighbor.
    private double mstSavingPercentage;

    /**
     * Constructor de RouteComparisonResult.
     *
     * @param nearestNeighborRoute ruta calculada con Nearest Neighbor.
     * @param mstBasedRoute ruta calculada con MST-Based.
     */
    public RouteComparisonResult(RouteResult nearestNeighborRoute, RouteResult mstBasedRoute) {
        this.nearestNeighborRoute = nearestNeighborRoute;
        this.mstBasedRoute = mstBasedRoute;

        selectBestRoute();
        calculateSavingPercentage();
    }

    /**
     * Selecciona la mejor ruta según la menor distancia total.
     *
     * Si ambas rutas tienen la misma distancia, se selecciona Nearest Neighbor
     * para mantener una decisión estable.
     */
    private void selectBestRoute() {
        if (nearestNeighborRoute.getTotalDistance() <= mstBasedRoute.getTotalDistance()) {
            selectedRoute = nearestNeighborRoute;
        } else {
            selectedRoute = mstBasedRoute;
        }
    }

    /**
     * Calcula el ahorro porcentual de MST-Based respecto a Nearest Neighbor.
     *
     * Fórmula:
     * ((distanciaNN - distanciaMST) * 100) / distanciaNN
     *
     * Si el resultado es positivo, MST-Based ahorró distancia.
     * Si es negativo, MST-Based fue peor que Nearest Neighbor.
     */
    private void calculateSavingPercentage() {
        int nearestDistance = nearestNeighborRoute.getTotalDistance();
        int mstDistance = mstBasedRoute.getTotalDistance();

        if (nearestDistance == 0 || nearestDistance == Integer.MAX_VALUE) {
            mstSavingPercentage = 0.0;
            return;
        }

        mstSavingPercentage =
                ((nearestDistance - mstDistance) * 100.0) / nearestDistance;
    }

    /**
     * Retorna la ruta de Nearest Neighbor.
     *
     * @return ruta NN.
     */
    public RouteResult getNearestNeighborRoute() {
        return nearestNeighborRoute;
    }

    /**
     * Retorna la ruta MST-Based.
     *
     * @return ruta MST-Based.
     */
    public RouteResult getMstBasedRoute() {
        return mstBasedRoute;
    }

    /**
     * Retorna la ruta seleccionada como mejor.
     *
     * @return ruta recomendada.
     */
    public RouteResult getSelectedRoute() {
        return selectedRoute;
    }

    /**
     * Retorna el porcentaje de ahorro de MST-Based respecto a Nearest Neighbor.
     *
     * @return ahorro porcentual.
     */
    public double getMstSavingPercentage() {
        return mstSavingPercentage;
    }

    /**
     * Retorna el nombre de la heurística seleccionada.
     *
     * @return nombre de la heurística ganadora.
     */
    public String getSelectedHeuristicName() {
        return selectedRoute.getHeuristicName();
    }
}
