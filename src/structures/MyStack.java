package structures;

/**
 * Pila genérica implementada manualmente.
 *
 * Una pila funciona con el principio LIFO:
 * Last In, First Out.
 *
 * Es decir, el último que entra es el primero que sale.
 *
 * Esta estructura será útil para DFS si se implementa de forma iterativa.
 *
 * @param <T> tipo de dato almacenado en la pila.
 */
public class MyStack<T> {

    // Nodo superior de la pila.
    private Node<T> top;

    // Cantidad de elementos en la pila.
    private int size;

    /**
     * Constructor de la pila.
     *
     * Al inicio está vacía.
     */
    public MyStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento en la cima de la pila.
     *
     * @param data dato que se desea insertar.
     */
    public void push(T data) {
        Node<T> newNode = new Node<>(data);

        // El nuevo nodo apunta al antiguo top.
        newNode.setNext(top);

        // El nuevo nodo se convierte en el top.
        top = newNode;

        size++;
    }

    /**
     * Extrae el elemento de la cima.
     *
     * @return dato extraído o null si la pila está vacía.
     */
    public T pop() {
        if (top == null) {
            return null;
        }

        T data = top.getData();

        // El nodo debajo del top pasa a ser el nuevo top.
        top = top.getNext();

        size--;

        return data;
    }

    /**
     * Consulta el elemento de la cima sin eliminarlo.
     *
     * @return dato superior o null si la pila está vacía.
     */
    public T peek() {
        if (top == null) {
            return null;
        }

        return top.getData();
    }

    /**
     * Indica si la pila está vacía.
     *
     * @return true si está vacía.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna la cantidad de elementos de la pila.
     *
     * @return tamaño de la pila.
     */
    public int size() {
        return size;
    }
}