package structures;

/**
 * Nodo usado por la cola de prioridad.
 *
 * Guarda un elemento genérico y una prioridad numérica.
 * En este proyecto, la prioridad normalmente será una distancia.
 *
 * @param <T> tipo de dato almacenado.
 */
public class PriorityNode<T> {

    // Elemento almacenado en la cola de prioridad.
    private T data;

    // Prioridad del elemento. Menor número significa mayor prioridad.
    private int priority;

    /**
     * Constructor del nodo de prioridad.
     *
     * @param data dato almacenado.
     * @param priority prioridad asociada al dato.
     */
    public PriorityNode(T data, int priority) {
        this.data = data;
        this.priority = priority;
    }

    /**
     * Retorna el dato almacenado.
     *
     * @return dato del nodo.
     */
    public T getData() {
        return data;
    }

    /**
     * Retorna la prioridad del dato.
     *
     * @return prioridad.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Cambia la prioridad del dato.
     *
     * @param priority nueva prioridad.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
}