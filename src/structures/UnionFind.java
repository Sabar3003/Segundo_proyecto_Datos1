package structures;

/**
 * Estructura Union-Find, también llamada Disjoint Set.
 *
 * Sirve para manejar conjuntos disjuntos.
 * Es especialmente útil para Kruskal, porque permite saber
 * si agregar una arista formaría un ciclo.
 */
public class UnionFind {

    // parent[i] guarda el representante o padre del elemento i.
    private int[] parent;

    // rank[i] ayuda a mantener los árboles más balanceados.
    private int[] rank;

    /**
     * Constructor de UnionFind.
     *
     * Al inicio, cada elemento está en su propio conjunto.
     *
     * @param size cantidad de elementos.
     */
    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];

        // Cada elemento inicia siendo su propio padre.
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Busca el representante del conjunto al que pertenece x.
     *
     * Usa compresión de caminos:
     * mientras busca el representante, va conectando los nodos
     * directamente con la raíz para acelerar futuras búsquedas.
     *
     * @param x elemento buscado.
     * @return representante del conjunto.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    /**
     * Une los conjuntos donde están x y y.
     *
     * @param x primer elemento.
     * @param y segundo elemento.
     * @return true si se unieron, false si ya estaban en el mismo conjunto.
     */
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        // Si tienen la misma raíz, ya pertenecen al mismo conjunto.
        if (rootX == rootY) {
            return false;
        }

        // Unión por rango:
        // El árbol más pequeño se conecta debajo del más grande.
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        return true;
    }

    /**
     * Verifica si dos elementos están en el mismo conjunto.
     *
     * @param x primer elemento.
     * @param y segundo elemento.
     * @return true si tienen el mismo representante.
     */
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}