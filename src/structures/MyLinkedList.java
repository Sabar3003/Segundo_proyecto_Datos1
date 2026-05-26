package structures;

/**
 * Lista enlazada simple implementada manualmente.
 *
 * Esta lista se usa porque el proyecto no permite usar estructuras
 * como ArrayList o LinkedList de java.util dentro del núcleo algorítmico.
 *
 * @param <T> tipo de dato que almacena la lista.
 */
public class MyLinkedList<T> {

    // Primer nodo de la lista.
    private Node<T> head;

    // Cantidad de elementos guardados en la lista.
    private int size;

    /**
     * Constructor de la lista.
     *
     * Al inicio la lista está vacía, por eso head es null
     * y size empieza en 0.
     */
    public MyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Agrega un elemento al final de la lista.
     *
     * Ejemplo:
     * Si la lista tiene A -> B
     * y agregamos C,
     * queda A -> B -> C.
     *
     * @param data dato que se desea agregar.
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);

        // Si la lista está vacía, el nuevo nodo se vuelve el primero.
        if (head == null) {
            head = newNode;
        } else {
            // Si no está vacía, recorremos hasta el último nodo.
            Node<T> current = head;

            while (current.getNext() != null) {
                current = current.getNext();
            }

            // El último nodo ahora apunta al nuevo nodo.
            current.setNext(newNode);
        }

        size++;
    }

    /**
     * Agrega un elemento al inicio de la lista.
     *
     * Ejemplo:
     * Si la lista tiene B -> C
     * y agregamos A al inicio,
     * queda A -> B -> C.
     *
     * @param data dato que se desea agregar.
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);

        // El nuevo nodo apunta al antiguo primero.
        newNode.setNext(head);

        // Ahora el nuevo nodo se convierte en el primero.
        head = newNode;

        size++;
    }

    /**
     * Obtiene el elemento ubicado en una posición específica.
     *
     * @param index posición que se desea consultar.
     * @return dato encontrado o null si el índice no existe.
     */
    public T get(int index) {
        // Validamos que el índice esté dentro del rango.
        if (index < 0 || index >= size) {
            return null;
        }

        Node<T> current = head;

        // Avanzamos nodo por nodo hasta llegar a la posición buscada.
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }

        return current.getData();
    }

    /**
     * Elimina y retorna el primer elemento de la lista.
     *
     * Este método será útil para estructuras como cola o pila.
     *
     * @return dato eliminado o null si la lista está vacía.
     */
    public T removeFirst() {
        if (head == null) {
            return null;
        }

        T data = head.getData();

        // El segundo nodo pasa a ser el primero.
        head = head.getNext();

        size--;

        return data;
    }

    /**
     * Retorna el primer nodo de la lista.
     *
     * Esto permite recorrer la lista desde otras clases.
     * Lo vamos a usar en Graph para recorrer los vecinos.
     *
     * @return primer nodo de la lista.
     */
    public Node<T> getHead() {
        return head;
    }

    /**
     * Indica si la lista está vacía.
     *
     * @return true si no tiene elementos.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna la cantidad de elementos de la lista.
     *
     * @return tamaño de la lista.
     */
    public int size() {
        return size;
    }
}