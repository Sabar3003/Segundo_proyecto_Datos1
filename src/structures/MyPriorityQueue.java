package structures;

/**
 * Cola de prioridad mínima implementada con heap binario.
 *
 * En esta estructura, el elemento con menor prioridad sale primero.
 *
 * Ejemplo:
 * Si tenemos:
 * A prioridad 10
 * B prioridad 3
 * C prioridad 7
 *
 * Primero sale B, porque tiene la prioridad más pequeña.
 *
 * Esta estructura será útil para algoritmos como Dijkstra y Prim.
 *
 * @param <T> tipo de dato almacenado.
 */
public class MyPriorityQueue<T> {

    // Arreglo que representa el heap.
    private PriorityNode<T>[] heap;

    // Cantidad actual de elementos en la cola.
    private int size;

    /**
     * Constructor de la cola de prioridad.
     *
     * @param capacity capacidad máxima inicial.
     */
    @SuppressWarnings("unchecked")
    public MyPriorityQueue(int capacity) {
        heap = new PriorityNode[capacity];
        size = 0;
    }

    /**
     * Inserta un nuevo dato con su prioridad.
     *
     * @param data dato a insertar.
     * @param priority prioridad asociada.
     * @return true si se insertó correctamente, false si no hay espacio.
     */
    public boolean insert(T data, int priority) {
        if (size >= heap.length) {
            return false;
        }

        // Insertamos el nuevo nodo al final del heap.
        heap[size] = new PriorityNode<>(data, priority);

        // Reordenamos hacia arriba para mantener la propiedad del heap.
        heapifyUp(size);

        size++;

        return true;
    }

    /**
     * Extrae el dato con menor prioridad.
     *
     * @return dato con menor prioridad o null si la cola está vacía.
     */
    public T extractMin() {
        if (isEmpty()) {
            return null;
        }

        // El mínimo siempre está en la raíz, posición 0.
        T minData = heap[0].getData();

        // Movemos el último elemento a la raíz.
        heap[0] = heap[size - 1];

        // Eliminamos la referencia del último elemento.
        heap[size - 1] = null;

        size--;

        // Reordenamos hacia abajo para mantener el heap.
        if (!isEmpty()) {
            heapifyDown(0);
        }

        return minData;
    }

    /**
     * Retorna la prioridad mínima sin eliminarla.
     *
     * @return prioridad mínima o -1 si está vacía.
     */
    public int peekPriority() {
        if (isEmpty()) {
            return -1;
        }

        return heap[0].getPriority();
    }

    /**
     * Revisa si la cola contiene un dato.
     *
     * Este método usa equals, por eso si luego guardan objetos personalizados,
     * deben tener cuidado con cómo comparan.
     *
     * @param data dato buscado.
     * @return true si el dato está en la cola.
     */
    public boolean contains(T data) {
        for (int i = 0; i < size; i++) {
            if (heap[i].getData().equals(data)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Disminuye la prioridad de un dato existente.
     *
     * Esto es útil en Dijkstra cuando encontramos un camino más corto.
     *
     * @param data dato al que se le quiere cambiar la prioridad.
     * @param newPriority nueva prioridad.
     * @return true si se actualizó, false si no se encontró.
     */
    public boolean decreasePriority(T data, int newPriority) {
        for (int i = 0; i < size; i++) {
            if (heap[i].getData().equals(data)) {

                // Solo disminuimos si la nueva prioridad es menor.
                if (newPriority < heap[i].getPriority()) {
                    heap[i].setPriority(newPriority);
                    heapifyUp(i);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Reordena un elemento hacia arriba.
     *
     * Se usa después de insertar o disminuir prioridad.
     *
     * @param index posición del elemento.
     */
    private void heapifyUp(int index) {
        int currentIndex = index;

        while (currentIndex > 0) {
            int parentIndex = getParentIndex(currentIndex);

            // Si el padre tiene menor o igual prioridad, el heap está bien.
            if (heap[parentIndex].getPriority() <= heap[currentIndex].getPriority()) {
                break;
            }

            // Si el hijo tiene menor prioridad, se intercambian.
            swap(parentIndex, currentIndex);

            currentIndex = parentIndex;
        }
    }

    /**
     * Reordena un elemento hacia abajo.
     *
     * Se usa después de extraer el mínimo.
     *
     * @param index posición del elemento.
     */
    private void heapifyDown(int index) {
        int currentIndex = index;

        while (true) {
            int leftChild = getLeftChildIndex(currentIndex);
            int rightChild = getRightChildIndex(currentIndex);
            int smallest = currentIndex;

            // Revisamos si el hijo izquierdo tiene menor prioridad.
            if (leftChild < size &&
                    heap[leftChild].getPriority() < heap[smallest].getPriority()) {
                smallest = leftChild;
            }

            // Revisamos si el hijo derecho tiene menor prioridad.
            if (rightChild < size &&
                    heap[rightChild].getPriority() < heap[smallest].getPriority()) {
                smallest = rightChild;
            }

            // Si el menor sigue siendo el actual, ya está ordenado.
            if (smallest == currentIndex) {
                break;
            }

            // Intercambiamos con el hijo de menor prioridad.
            swap(currentIndex, smallest);

            currentIndex = smallest;
        }
    }

    /**
     * Intercambia dos posiciones del heap.
     *
     * @param firstIndex primera posición.
     * @param secondIndex segunda posición.
     */
    private void swap(int firstIndex, int secondIndex) {
        PriorityNode<T> temp = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temp;
    }

    /**
     * Obtiene el índice del padre.
     *
     * @param index índice del hijo.
     * @return índice del padre.
     */
    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    /**
     * Obtiene el índice del hijo izquierdo.
     *
     * @param index índice del padre.
     * @return índice del hijo izquierdo.
     */
    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    /**
     * Obtiene el índice del hijo derecho.
     *
     * @param index índice del padre.
     * @return índice del hijo derecho.
     */
    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    /**
     * Indica si la cola está vacía.
     *
     * @return true si no tiene elementos.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retorna la cantidad de elementos.
     *
     * @return tamaño actual.
     */
    public int size() {
        return size;
    }
}