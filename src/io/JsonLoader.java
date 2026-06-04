package io;

import com.google.gson.Gson;
import graph.Graph;
import graph.Vertex;
import model.Package;
import model.Truck;

import java.io.FileReader;
import java.io.IOException;

/**
 * Clase JsonLoader.
 *
 * Esta clase se encarga de leer el archivo JSON del proyecto y convertirlo
 * en objetos reales de Java.
 *
 * Desde el JSON se cargan:
 * - Los vértices de la ciudad.
 * - Las aristas de la ciudad.
 * - El depósito.
 * - Los paquetes.
 * - Los camiones.
 *
 * Esta clase pertenece al package io porque su responsabilidad principal
 * es entrada de datos, no algoritmos ni planificación.
 */
public class JsonLoader {

    /**
     * Carga todo el caso de prueba desde un archivo JSON.
     *
     * @param filePath ruta del archivo JSON.
     * @return objeto LogisticsData con grafo, depósito, paquetes y camiones.
     * @throws IOException si el archivo no existe o no puede leerse.
     */
    public static LogisticsData load(String filePath) throws IOException {

        /*
         * Gson es la librería externa permitida para interpretar JSON.
         * Convierte el archivo JSON en objetos auxiliares de Java.
         */
        Gson gson = new Gson();

        /*
         * FileReader abre el archivo usando la ruta recibida.
         * El try-with-resources cierra el archivo automáticamente al terminar.
         */
        try (FileReader reader = new FileReader(filePath)) {

            /*
             * JsonRoot es una clase interna auxiliar que tiene la misma forma
             * del archivo JSON. Gson llena este objeto automáticamente.
             */
            JsonRoot root = gson.fromJson(reader, JsonRoot.class);

            /*
             * Se valida que el JSON tenga las secciones principales.
             * Si falta algo importante, se detiene la carga con un mensaje claro.
             */
            validateRoot(root);

            /*
             * Se construye el grafo usando la cantidad de vértices del JSON.
             * Esto permite que el tamaño del arreglo interno sea exacto.
             */
            Graph graph = new Graph(root.ciudad.vertices.length);

            /*
             * Primero se agregan todos los vértices.
             * Esto es necesario porque una arista solo puede agregarse si sus
             * dos extremos ya existen dentro del grafo.
             */
            for (int i = 0; i < root.ciudad.vertices.length; i++) {
                JsonVertex jsonVertex = root.ciudad.vertices[i];

                validateVertex(jsonVertex, i);

                Vertex vertex = new Vertex(
                        jsonVertex.id,
                        jsonVertex.tipo,
                        jsonVertex.x,
                        jsonVertex.y
                );

                boolean added = graph.addVertex(vertex);

                if (!added) {
                    throw new IllegalArgumentException(
                            "No se pudo agregar el vértice " + jsonVertex.id
                                    + ". Puede estar repetido o superar la capacidad del grafo."
                    );
                }
            }

            /*
             * Luego se agregan las aristas.
             * El método addEdge de Graph ya se encarga de guardar la conexión
             * en ambos sentidos porque el grafo es no dirigido.
             */
            for (int i = 0; i < root.ciudad.aristas.length; i++) {
                JsonEdge jsonEdge = root.ciudad.aristas[i];

                validateEdge(jsonEdge, i);

                boolean added = graph.addEdge(
                        jsonEdge.u,
                        jsonEdge.v,
                        jsonEdge.distancia
                );

                if (!added) {
                    throw new IllegalArgumentException(
                            "No se pudo agregar la arista "
                                    + jsonEdge.u + " - " + jsonEdge.v
                                    + ". Revise que ambos vértices existan y que la distancia sea positiva."
                    );
                }
            }

            /*
             * Se busca el depósito.
             * El proyecto exige que exista un único depósito marcado en el grafo.
             */
            String depotId = findDepotId(graph);

            if (depotId == null) {
                throw new IllegalArgumentException(
                        "El JSON no contiene ningún vértice de tipo DEPOT."
                );
            }

            /*
             * Se construye el arreglo de paquetes del modelo real.
             */
            Package[] packages = new Package[root.paquetes.length];

            for (int i = 0; i < root.paquetes.length; i++) {
                JsonPackage jsonPackage = root.paquetes[i];

                validatePackage(jsonPackage, i, graph);

                packages[i] = new Package(
                        jsonPackage.id,
                        jsonPackage.destino,
                        jsonPackage.peso,
                        jsonPackage.prioridad
                );
            }

            /*
             * Se construye el arreglo de camiones.
             * Cada camión recibe como capacidad máxima de paquetes packages.length,
             * porque en el peor caso un solo camión podría recibir todos los paquetes.
             */
            Truck[] trucks = new Truck[root.camiones.length];

            for (int i = 0; i < root.camiones.length; i++) {
                JsonTruck jsonTruck = root.camiones[i];

                validateTruck(jsonTruck, i);

                trucks[i] = new Truck(
                        jsonTruck.id,
                        jsonTruck.capacidad,
                        packages.length
                );
            }

            /*
             * Finalmente se retornan todos los datos juntos dentro de LogisticsData.
             */
            return new LogisticsData(graph, depotId, packages, trucks);
        }
    }

    /**
     * Valida que el objeto principal del JSON tenga las secciones necesarias.
     *
     * @param root objeto raíz cargado por Gson.
     */
    private static void validateRoot(JsonRoot root) {
        if (root == null) {
            throw new IllegalArgumentException("El archivo JSON está vacío o mal formado.");
        }

        if (root.ciudad == null) {
            throw new IllegalArgumentException("El JSON no contiene la sección 'ciudad'.");
        }

        if (root.ciudad.vertices == null || root.ciudad.vertices.length == 0) {
            throw new IllegalArgumentException("El JSON no contiene vértices.");
        }

        if (root.ciudad.aristas == null || root.ciudad.aristas.length == 0) {
            throw new IllegalArgumentException("El JSON no contiene aristas.");
        }

        if (root.paquetes == null) {
            throw new IllegalArgumentException("El JSON no contiene la sección 'paquetes'.");
        }

        if (root.camiones == null) {
            throw new IllegalArgumentException("El JSON no contiene la sección 'camiones'.");
        }
    }

    /**
     * Valida un vértice individual.
     *
     * @param vertex vértice leído desde JSON.
     * @param index posición del vértice en el arreglo JSON.
     */
    private static void validateVertex(JsonVertex vertex, int index) {
        if (vertex == null) {
            throw new IllegalArgumentException("El vértice en posición " + index + " es null.");
        }

        if (isEmpty(vertex.id)) {
            throw new IllegalArgumentException("El vértice en posición " + index + " no tiene id.");
        }

        if (isEmpty(vertex.tipo)) {
            throw new IllegalArgumentException("El vértice " + vertex.id + " no tiene tipo.");
        }
    }

    /**
     * Valida una arista individual.
     *
     * @param edge arista leída desde JSON.
     * @param index posición de la arista en el arreglo JSON.
     */
    private static void validateEdge(JsonEdge edge, int index) {
        if (edge == null) {
            throw new IllegalArgumentException("La arista en posición " + index + " es null.");
        }

        if (isEmpty(edge.u) || isEmpty(edge.v)) {
            throw new IllegalArgumentException(
                    "La arista en posición " + index + " debe tener vértices u y v."
            );
        }

        if (edge.distancia <= 0) {
            throw new IllegalArgumentException(
                    "La arista " + edge.u + " - " + edge.v
                            + " tiene una distancia inválida."
            );
        }
    }

    /**
     * Valida un paquete individual.
     *
     * @param packageData paquete leído desde JSON.
     * @param index posición del paquete en el arreglo JSON.
     * @param graph grafo ya cargado.
     */
    private static void validatePackage(JsonPackage packageData, int index, Graph graph) {
        if (packageData == null) {
            throw new IllegalArgumentException("El paquete en posición " + index + " es null.");
        }

        if (isEmpty(packageData.id)) {
            throw new IllegalArgumentException("El paquete en posición " + index + " no tiene id.");
        }

        if (isEmpty(packageData.destino)) {
            throw new IllegalArgumentException("El paquete " + packageData.id + " no tiene destino.");
        }

        if (graph.getVertexById(packageData.destino) == null) {
            throw new IllegalArgumentException(
                    "El paquete " + packageData.id
                            + " tiene como destino un vértice que no existe: "
                            + packageData.destino
            );
        }

        if (packageData.peso <= 0) {
            throw new IllegalArgumentException(
                    "El paquete " + packageData.id + " tiene peso inválido."
            );
        }

        if (packageData.prioridad < 1 || packageData.prioridad > 3) {
            throw new IllegalArgumentException(
                    "El paquete " + packageData.id
                            + " tiene prioridad inválida. Debe ser 1, 2 o 3."
            );
        }
    }

    /**
     * Valida un camión individual.
     *
     * @param truck camión leído desde JSON.
     * @param index posición del camión en el arreglo JSON.
     */
    private static void validateTruck(JsonTruck truck, int index) {
        if (truck == null) {
            throw new IllegalArgumentException("El camión en posición " + index + " es null.");
        }

        if (isEmpty(truck.id)) {
            throw new IllegalArgumentException("El camión en posición " + index + " no tiene id.");
        }

        if (truck.capacidad <= 0) {
            throw new IllegalArgumentException(
                    "El camión " + truck.id + " tiene capacidad inválida."
            );
        }
    }

    /**
     * Busca el vértice marcado como DEPOT.
     *
     * @param graph grafo cargado.
     * @return id del depósito o null si no existe.
     */
    private static String findDepotId(Graph graph) {
        String depotId = null;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            Vertex vertex = graph.getVertex(i);

            if (vertex != null && vertex.isDepot()) {
                /*
                 * Si ya se había encontrado un depósito antes, significa que
                 * el JSON tiene más de un DEPOT, lo cual no es válido.
                 */
                if (depotId != null) {
                    throw new IllegalArgumentException(
                            "El JSON contiene más de un vértice tipo DEPOT."
                    );
                }

                depotId = vertex.getId();
            }
        }

        return depotId;
    }

    /**
     * Verifica si un texto está vacío.
     *
     * @param text texto a revisar.
     * @return true si el texto es null o está vacío.
     */
    private static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /*
     * Las siguientes clases internas representan la estructura del archivo JSON.
     *
     * No forman parte del modelo principal del proyecto.
     * Solo existen para que Gson pueda leer el archivo y luego JsonLoader
     * convierta esos datos a Graph, Package y Truck.
     */

    private static class JsonRoot {
        JsonCity ciudad;
        JsonPackage[] paquetes;
        JsonTruck[] camiones;
    }

    private static class JsonCity {
        JsonVertex[] vertices;
        JsonEdge[] aristas;
    }

    private static class JsonVertex {
        String id;
        String tipo;
        int x;
        int y;
    }

    private static class JsonEdge {
        String u;
        String v;
        int distancia;
    }

    private static class JsonPackage {
        String id;
        String destino;
        int peso;
        int prioridad;
    }

    private static class JsonTruck {
        String id;
        int capacidad;
    }
}
