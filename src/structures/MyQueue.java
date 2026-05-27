package structures;

/**
 * Cola genérica implementada manualmente.
 *
 * Una cola funciona con el principio FIFO:
 * First In, First Out.
 *
 * Es decir, el primero que entra es el primero que sale.
 *
 * Esta estructura será útil para BFS.
 *
 * @param <T> tipo de dato almacenado en la cola.
 */
public class MyQueue<T> {

    // Primer nodo de la cola.
    private Node<T> front;

    // Último nodo de la cola.
    private Node<T> rear;

    // Cantidad de elementos en la cola.
    private int size;

    /**
     * Constructor de la cola.
     *
     * Al inicio está vacía.
     */
    public MyQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Inserta un elemento al final de la cola.
     *
     * @param data dato que se desea insertar.
     */
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);

        // Si la cola está vacía, el nuevo nodo es primero y último.
        if (rear == null) {
            front = newNode;
            rear = newNode;
        } else {
            // Si ya hay elementos, el último apunta al nuevo.
            rear.setNext(newNode);

            // El nuevo nodo pasa a ser el último.
            rear = newNode;
        }

        size++;
    }

    /**
     * Extrae el primer elemento de la cola.
     *
     * @return dato extraído o null si la cola está vacía.
     */
    public T dequeue() {
        if (front == null) {
            return null;
        }

        T data = front.getData();

        // El segundo nodo pasa a ser el primero.
        front = front.getNext();

        // Si después de sacar el elemento la cola queda vacía,
        // también limpiamos rear.
        if (front == null) {
            rear = null;
        }

        size--;

        return data;
    }

    /**
     * Consulta el primer elemento sin eliminarlo.
     *
     * @return dato del frente o null si está vacía.
     */
    public T peek() {
        if (front == null) {
            return null;
        }

        return front.getData();
    }

    /**
     * Indica si la cola está vacía.
     *
     * @return true si está vacía.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna la cantidad de elementos.
     *
     * @return tamaño de la cola.
     */
    public int size() {
        return size;
    }
}