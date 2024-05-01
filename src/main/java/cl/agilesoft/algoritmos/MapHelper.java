package cl.agilesoft.algoritmos;

import cl.agilesoft.algoritmos.dto.MyMap;
import cl.agilesoft.algoritmos.dto.Node;
import cl.agilesoft.algoritmos.dto.Tour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MapHelper {

    private MapHelper() {
    }

    public static MyMap createMap(String fileName) throws IOException {
        InputStream inputStream = MapHelper.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("El archivo " + fileName + " no se encontró en el directorio resources.");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<int[]> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }
                rows.add(row);
            }
            // Convertir la lista de arreglos en una matriz de enteros
            int[][] matrix = new int[rows.size()][];
            for (int i = 0; i < rows.size(); i++) {
                matrix[i] = rows.get(i);
            }
            return new MyMap(fileName, matrix);
        }
    }

    public static Tour createTour(MyMap map) {
        int[] randomRoute = generateRandomRoute(map.getDistancesMap().length);
        /*
        System.out.print("\nRuta aleatorea: [");
        for (int i = 0; i < randomRoute.length; i++) {
            System.out.print(randomRoute[i] + ", ");
        }
        System.out.print("]\n");
         */
        Node[] nodesRoute = new Node[randomRoute.length];
        Node nextNode = null;
        for (int i = 0; i < randomRoute.length - 1; i++) {
            int pos = randomRoute[i];
            Node actualNode = nodesRoute[pos];
            if (actualNode == null) {
                actualNode = new Node(pos, null, null, i);
                nodesRoute[pos] = actualNode;
            }
            pos = randomRoute[i + 1];
            nextNode = new Node(pos, null, actualNode, i + 1);
            actualNode.next = nextNode;
            nodesRoute[pos] = nextNode;
        }
        nextNode.next = nodesRoute[0];
        nodesRoute[0].previous = nextNode;
        return new Tour(map, nodesRoute);
    }

    public static int[] generateRandomRoute(int cityCount) {
        List<Integer> route = new ArrayList<>();
        for (int i = 1; i < cityCount; i++) {
            route.add(i);
        }
        Collections.shuffle(route, new Random());
        route.addFirst(0);
        return route.stream().mapToInt(Integer::intValue).toArray();
    }

}
