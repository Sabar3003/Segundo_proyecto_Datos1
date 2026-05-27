package structures;

/**
 * Nodo genérico para estructuras enlazadas.
 *
 * Esta clase será la base para construir listas, colas y pilas.
 * Es genérica porque puede guardar cualquier tipo de dato:
 * String, Integer, Vertex, Edge, etc.
 *
 * @param <T> tipo de dato que se almacena en el nodo.
 */
public class Node<T> {

    // Dato almacenado dentro del nodo.
    private T data;

    // Referencia al siguiente nodo de la estructura.
    private Node<T> next;

    /**
     * Constructor del nodo.
     *
     * Cuando se crea un nodo, recibe un dato y al inicio
     * no apunta a ningún otro nodo.
     *
     * @param data dato que se desea guardar.
     */
    public Node(T data) {
        this.data = data;
        this.next = null;
    }

    /**
     * Retorna el dato almacenado en el nodo.
     *
     * @return dato del nodo.
     */
    public T getData() {
        return data;
    }

    /**
     * Cambia el dato almacenado en el nodo.
     *
     * @param data nuevo dato.
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Retorna el siguiente nodo.
     *
     * @return referencia al siguiente nodo.
     */
    public Node<T> getNext() {
        return next;
    }

    /**
     * Cambia la referencia al siguiente nodo.
     *
     * @param next nuevo nodo siguiente.
     */
    public void setNext(Node<T> next) {
        this.next = next;
    }
}